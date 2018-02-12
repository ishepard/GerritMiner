package org.davidespadini.gerrit;

public class Review {
	String gerritId, changeId, author, createdAt, submittedAt, updatedAt, filename, project;
	int numComments, numReviewers, numFiles;
	public Review(String gerritId, String changeId, String author, String createdAt, String submittedAt,
			String updatedAt, String filename, String project, int numComments, int numReviewers, int numFiles) {
		super();
		this.gerritId = gerritId;
		this.changeId = changeId;
		this.author = author;
		this.createdAt = createdAt;
		this.submittedAt = submittedAt;
		this.updatedAt = updatedAt;
		this.filename = filename;
		this.project = project;
		this.numComments = numComments;
		this.numReviewers = numReviewers;
		this.numFiles = numFiles;
	}
	public String getGerritId() {
		return gerritId;
	}
	public void setGerritId(String gerritId) {
		this.gerritId = gerritId;
	}
	public String getChangeId() {
		return changeId;
	}
	public void setChangeId(String changeId) {
		this.changeId = changeId;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getSubmittedAt() {
		return submittedAt;
	}
	public void setSubmittedAt(String submittedAt) {
		this.submittedAt = submittedAt;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public int getNumComments() {
		return numComments;
	}
	public void setNumComments(int numComments) {
		this.numComments = numComments;
	}
	public int getNumReviewers() {
		return numReviewers;
	}
	public void setNumReviewers(int numReviewers) {
		this.numReviewers = numReviewers;
	}
	public int getNumFiles() {
		return numFiles;
	}
	public void setNumFiles(int numFiles) {
		this.numFiles = numFiles;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((changeId == null) ? 0 : changeId.hashCode());
		result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + ((filename == null) ? 0 : filename.hashCode());
		result = prime * result + ((gerritId == null) ? 0 : gerritId.hashCode());
		result = prime * result + numComments;
		result = prime * result + numFiles;
		result = prime * result + numReviewers;
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		result = prime * result + ((submittedAt == null) ? 0 : submittedAt.hashCode());
		result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Review other = (Review) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (changeId == null) {
			if (other.changeId != null)
				return false;
		} else if (!changeId.equals(other.changeId))
			return false;
		if (createdAt == null) {
			if (other.createdAt != null)
				return false;
		} else if (!createdAt.equals(other.createdAt))
			return false;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		if (gerritId == null) {
			if (other.gerritId != null)
				return false;
		} else if (!gerritId.equals(other.gerritId))
			return false;
		if (numComments != other.numComments)
			return false;
		if (numFiles != other.numFiles)
			return false;
		if (numReviewers != other.numReviewers)
			return false;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		if (submittedAt == null) {
			if (other.submittedAt != null)
				return false;
		} else if (!submittedAt.equals(other.submittedAt))
			return false;
		if (updatedAt == null) {
			if (other.updatedAt != null)
				return false;
		} else if (!updatedAt.equals(other.updatedAt))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Review [gerritId=" + gerritId + ", changeId=" + changeId + ", author=" + author + ", createdAt="
				+ createdAt + ", submittedAt=" + submittedAt + ", updatedAt=" + updatedAt + ", filename=" + filename
				+ ", project=" + project + ", numComments=" + numComments + ", numReviewers=" + numReviewers
				+ ", numFiles=" + numFiles + "]";
	}
	
		
}
