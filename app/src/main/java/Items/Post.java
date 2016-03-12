package Items;

/**
 * Created by Mahdi on 2/7/2016.
 */
public class Post {

    private int id;

    private int post_mysql_id;

    private String posterName;
    private String posterAvatar;
    private String posterDegree;
    private int posterMysqlID; // mysql id

    private String postTitle;
    private String postDesc;

    private int votes;
    private int answers;
    private int views;


    private int voteType; // Represents whether the current user voted or not
    private boolean bookmarked;
    private boolean isRead;
    private boolean isMine;
    private boolean published;
    private boolean Locked;
    private boolean isQuestion;

    private int questionMySqlID;

    private String date;
    private String time;

    private String tags;

    private String reserved1;
    private String reserved2;
    private String reserved3;



    public Post() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPost_mysql_id() {
        return post_mysql_id;
    }

    public void setPost_mysql_id(int post_mysql_id) {
        this.post_mysql_id = post_mysql_id;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public String getPosterAvatar() {
        return posterAvatar;
    }

    public void setPosterAvatar(String posterAvatar) {
        this.posterAvatar = posterAvatar;
    }

    public String getPosterDegree() {
        return posterDegree;
    }

    public void setPosterDegree(String posterDegree) {
        this.posterDegree = posterDegree;
    }

    public int getPosterMysqlID() {
        return posterMysqlID;
    }

    public void setPosterMysqlID(int posterMysqlID) {
        this.posterMysqlID = posterMysqlID;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getAnswers() {
        return answers;
    }

    public void setAnswers(int answers) {
        this.answers = answers;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getVoteType() {
        return voteType;
    }

    public void setVoteType(int voteType) {
        this.voteType = voteType;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isLocked() {
        return Locked;
    }

    public void setLocked(boolean locked) {
        Locked = locked;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }

    public String getReserved3() {
        return reserved3;
    }

    public void setReserved3(String reserved3) {
        this.reserved3 = reserved3;
    }

    public boolean isQuestion()
    {
        return isQuestion;
    }

    public void setQuestion(boolean question) {
        this.isQuestion = question;
    }

    public int getQuestionMySqlID() {
        return questionMySqlID;
    }

    public void setQuestionMySqlID(int questionMySqlID) {
        this.questionMySqlID = questionMySqlID;
    }
}
