package org.davidespadini.gerrit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.changes.Changes;
import com.google.gerrit.extensions.client.ListChangesOption;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.common.CommentInfo;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.common.ReviewerInfo;
import com.google.gerrit.extensions.common.RevisionInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;

public class GerritMiner {
	Connection conn;
	Statement stmt;
	String databaseReviews;
	String databaseComments;
	String databaseReviewers;
	String connectionUrl;
	String databaseStatus;
	
	public GerritMiner() {
		super();
	}

	public GerritMiner(Connection conn, String databaseName, String connectionUrl) throws SQLException {
		super();
		this.conn = conn;
		this.databaseReviews = databaseName + ".reviews";
		this.databaseComments = databaseName + ".comments";
		this.databaseReviewers = databaseName + ".reviewers";
		this.databaseStatus = databaseName + ".status";
		this.connectionUrl = connectionUrl;
		this.stmt = conn.createStatement();
	}

	public void start(int startpoint, int endpoint) throws RestApiException, SQLException{
		GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();

		GerritAuthData.Basic authData = new GerritAuthData.Basic(connectionUrl);
		GerritApi gerritApi = gerritRestApiFactory.create(authData);
		Changes changes = gerritApi.changes();

    	while (startpoint >= endpoint){
			mine(changes, startpoint);
    		startpoint--;
    	}
	}

	private void mine(Changes changes, int id) throws RestApiException, SQLException {
		if (isAlreadyPresent(id)){
			System.out.println(id + " is already present!");
			return;
		}
		List<ChangeInfo> reviews = getChangesInRange(changes, id);

		if (reviews.isEmpty()){
			return;
		}

		for (ChangeInfo c : reviews){
			Change newChange = getCommentsPerReview(changes, id, reviews, c);
			List<ReviewerInfo> revs = getReviewers(changes, c);
			
			store(c, newChange, revs, id);
		}
	}

	private Change getCommentsPerReview(Changes changes, int id, List<ChangeInfo> reviews, ChangeInfo c) throws SQLException, RestApiException {
		System.out.println("#" + id + " changeID: " + c.changeId + "; submitted at: "  + c.submitted + " by " + getAuthor(c));

		// Getting the revisions
		Map<String, RevisionInfo> m = c.revisions;
		
		HashMap<String, Integer> totComments = new HashMap<String, Integer>();
		HashMap<String, String> bodyComments = new HashMap<String, String>();
		
		for (Map.Entry<String, RevisionInfo> entry : m.entrySet()){
			// Getting the list of the files per revision
			Map<String, FileInfo> filesMap = entry.getValue().files;
			
			if (filesMap == null){
				continue;
			}
			Set<String> files = filesMap.keySet();
			
			String revisionId = entry.getKey();
			System.out.println("ChangeID: " + c.changeId + ", revisionID: " + revisionId + ", files: " + files.toString());
			
			Map<String, List<CommentInfo>> comments = getCommentPerRevision(changes, c, revisionId);

			// Updating the total number of comments
			HashMap<String, Integer> currentNumComments = getNumberOfComments(files, comments);
			totComments = updateComments(totComments, currentNumComments);

			// Updating the total body of the comments 
			HashMap<String, String> currentBodyComments = getCommentsBody(files, comments);
			bodyComments = updateBodyComments(bodyComments, currentBodyComments);
		}
		return new Change(totComments, bodyComments);
	}

	private List<ReviewerInfo> getReviewers(Changes changes, ChangeInfo c) throws RestApiException {
		List<ReviewerInfo> res = new ArrayList<ReviewerInfo>();
		int numTries = 1;
		while (true){
			try{
				// Getting the list of comments for each file
				res = changes.id(c._number).listReviewers();
				break;
			} catch(com.urswolfer.gerrit.client.rest.http.HttpStatusException e){
				System.out.println("Unable to get the list of the reviewers");
				break;
			} catch (com.google.gerrit.extensions.restapi.RestApiException e){
				if (numTries > 100){
					System.out.println("Too many tries! Quitting...");
					System.out.println("Sort key of the last element: " + c._sortkey);
					System.exit(-1);
				}
				numTries++;
				System.out.println("--------REQUEST FAILED: doing a new request. Request number " + numTries);
			}
		}
		return res;
	}

	private Map<String, List<CommentInfo>> getCommentPerRevision(Changes changes, ChangeInfo c, String revisionId){
		Map<String, List<CommentInfo>> comments = new HashMap<String, List<CommentInfo>>();
		int numTries = 1;
		while (true){
			try{
				// Getting the list of comments for each file
				comments = getComments(changes, c, revisionId);
				break;
			} catch(com.urswolfer.gerrit.client.rest.http.HttpStatusException e){
				System.out.println("Unable to get detail of change " + c.changeId);
				break;
			} catch (com.google.gerrit.extensions.restapi.RestApiException e){
				if (numTries > 100){
					System.out.println("Too many tries! Quitting...");
					System.out.println("Sort key of the last element: " + c._sortkey);
					System.exit(-1);
				}
				numTries++;
				System.out.println("--------REQUEST FAILED: doing a new request. Request number " + numTries);
			}
		}
		return comments;
	}

	private List<ChangeInfo> getChangesInRange(Changes changes, int id) {
		List<ChangeInfo> reviews = null;
		int numTries = 1;
		
		while (true){
			try{
				reviews = changes.query(Integer.toString(id)).withOptions(ListChangesOption.ALL_FILES, ListChangesOption.ALL_REVISIONS, ListChangesOption.DETAILED_ACCOUNTS).get();
				break;
			} catch(com.google.gerrit.extensions.restapi.RestApiException e){
				if (numTries > 100){
					System.out.println("Too many tries! Quitting...");
					System.exit(-1);
				}
				numTries++;
				System.out.println("--------REQUEST FAILED: doing a new request. Request number " + numTries);
			}
		}
		return reviews;
		
		// FOR TESTING
//		try {
//			reviews = changes.query(Integer.toString(id)).withOptions(ListChangesOption.ALL_FILES, ListChangesOption.ALL_REVISIONS, ListChangesOption.DETAILED_ACCOUNTS).get();
////			reviews = changes.query("I47de85fc2985dc6c46e31f571525b37f6ed8b23f").withOptions(ListChangesOption.ALL_FILES, ListChangesOption.ALL_REVISIONS, ListChangesOption.DETAILED_ACCOUNTS).get();
//		} catch (RestApiException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private Map<String, List<CommentInfo>> getComments(Changes changes, ChangeInfo c, String revisionId)
			throws RestApiException {
		return changes.id(c._number).revision(revisionId).comments();
	}

	private String getAuthor(ChangeInfo c) {
		if (c.owner.name != null){
			return formatString(c.owner.name);
		}
		return null;
	}
	
	public HashMap<String, String> getCommentsBody(Set<String> files, Map<String, List<CommentInfo>> comments) {
		HashMap<String, String> commentsBody = new HashMap<String, String>();
		for (String file: files){
			List<CommentInfo> commentsList = comments.getOrDefault(file, null);

			if (commentsList != null){
				commentsBody.put(file, String.join("\n###NEWCOMMENT###\n", commentsList.stream().map(x -> x.message).toArray(size -> new String[size])));
			}
		}
		return commentsBody;
	}

	private HashMap<String, Integer> updateComments(HashMap<String, Integer> totComments, HashMap<String, Integer> currentComments) {
		for (String file : currentComments.keySet()){
			totComments.put(file, totComments.getOrDefault(file, 0) + currentComments.getOrDefault(file, 0));
		}
		return totComments;
	}
	
	private HashMap<String, String> updateBodyComments(HashMap<String, String> bodyComments, HashMap<String, String> currentBodyComments) {
		for (String file : currentBodyComments.keySet()){
			bodyComments.put(file, bodyComments.getOrDefault(file, "") + "\n###NEWREVISION###\n" + currentBodyComments.getOrDefault(file, ""));
		}
		return bodyComments;
	}

	public Integer insertReview(Review r) throws SQLException{
		String query = "INSERT INTO " + databaseReviews + " (gerritid, changeid, author, created, submitted, updated, file, project, numfiles, numreviewers, numcomments) VALUES('" + r.getGerritId() 
				+ "', '" + r.changeId
				+ "', '" + r.author 
				+ "', '" + r.createdAt
				+ "', '" + r.submittedAt
				+ "', '" + r.updatedAt
				+ "', '" + r.filename
				+ "', '" + r.project
				+ "', '" + r.numFiles
				+ "', '" + r.numReviewers
				+ "', " + r.numComments 
				+ ")";
		stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
		ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
        }
        else {
            throw new SQLException("Creating review failed: no ID obtained.");
        }
	}
	
	public void insertComments(String changeId, Integer id, String bodyComments ) throws SQLException{
		String formattedComment = formatString(bodyComments);
		String query = "INSERT INTO " + databaseComments + " (id, changeid, body) VALUES('" + id  
				+ "', '" + changeId 
				+ "', '" + formattedComment
				+ "')";
		stmt.executeUpdate(query);
	}
	
	public void insertReviewer(String changeId, String reviewer, String email) throws SQLException{
		String query = "INSERT INTO " + databaseReviewers + " (changeid, reviewer, email) VALUES('"  
				+ changeId 
				+ "', '" + reviewer
				+ "', '" + email
				+ "')";
		stmt.executeUpdate(query);
		
	}
	
	public void insertStatus(String gerritId, String status) throws SQLException{
		String query = "INSERT INTO " + databaseStatus + " (id, status) VALUES('"  
				+ gerritId
				+ "', '" + status
				+ "')";
		stmt.executeUpdate(query);
		
	}
	
	public HashMap<String, Integer> getNumberOfComments(Set<String> files, Map<String, List<CommentInfo>> comments){
		HashMap<String, Integer> totComments = new HashMap<String, Integer>();
		for (String file: files){
			List<CommentInfo> comment = comments.getOrDefault(file, null);
			if (comment != null){
				totComments.put(file, comment.size());
			} else {
				totComments.put(file, 0);
			}
		}
		return totComments;
	}
	
	public String formatString(String toFormat){
		if (toFormat == null)
			return "";
		return toFormat.replaceAll("\\'", "").replaceAll("\"", "").replaceAll("%", "").replace("\\", "");
	}
	
	private void store(ChangeInfo c, Change newChange, List<ReviewerInfo> revs, int id) throws SQLException {
		HashMap<String, Integer> fileIds = new HashMap<String, Integer>();
		
		if (isAlreadyPresent(newChange, c)){
			System.out.println(c.changeId + " already present! ");
			return;
		}
		for (String file: newChange.getTotComments().keySet()){
			String submitted = (c.submitted != null) ? c.submitted.toString() : "null";
			Review r = new Review(Integer.toString(id), c.changeId, getAuthor(c), c.created.toString(), submitted, c.updated.toString(), formatString(file), formatString(c.project), newChange.getTotComments().get(file), revs.size(), newChange.getTotComments().size());
			fileIds.put(file, insertReview(r));
		}
		
		for (String file : newChange.getBodyComments().keySet()){
			insertComments(c.changeId, fileIds.get(file), newChange.getBodyComments().get(file));
		}
		
		for (ReviewerInfo ri : revs){
			insertReviewer(c.changeId, formatString(ri.name), formatString(ri.email));
		}
		insertStatus(Integer.toString(id), c.status == null ? "UNKNOWN" : c.status.toString());
	}
	

	private boolean isAlreadyPresent(Change newChange, ChangeInfo c) throws SQLException {
		for (String file: newChange.getTotComments().keySet()){
			PreparedStatement psReview = conn.prepareStatement("SELECT * FROM reviews where changeid = ? and file = ?");
		    psReview.setString(1, c.changeId);
		    psReview.setString(2, file);
		    ResultSet rs = psReview.executeQuery();
		    
		    if (rs.next())
		    	return true;
		}
		return false;

		
	}
	
	private boolean isAlreadyPresent(int id) throws SQLException {
		PreparedStatement psReview = conn.prepareStatement("SELECT * FROM reviews where gerritid = ?");
	    psReview.setInt(1, id);
	    ResultSet rs = psReview.executeQuery();
	    
	    if (rs.next())
	    	return true;
		
		return false;
	}
}
