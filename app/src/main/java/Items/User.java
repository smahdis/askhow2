package Items;

/**
 * Created by Mahdi on 2/10/2016.
 */
public class User {

    private int id;
    private int mysql_id;
    public String UserName;
    public String FirstName;
    public String LastName;
    private String Avatar;
    private String Phone;
    private String Email;
    private String Profession;
    private String Degree;
    private String Status;

    private String City;

    private int NumberOfPosts;
    private int Points;
    private int Activation_Status;

    private String RegDate;
    private String LastLoginDate;
    private String LastLoginTime;

    private String Reserved1;
    private String Reserved2;
    private String Reserved3;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMysql_id() {
        return mysql_id;
    }

    public void setMysql_id(int mysql_id) {
        this.mysql_id = mysql_id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getProfession() {
        return Profession;
    }

    public void setProfession(String profession) {
        Profession = profession;
    }

    public String getDegree() {
        return Degree;
    }

    public void setDegree(String degree) {
        Degree = degree;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public int getNumberOfPosts() {
        return NumberOfPosts;
    }

    public void setNumberOfPosts(int numberOfPosts) {
        NumberOfPosts = numberOfPosts;
    }

    public int getPoints() {
        return Points;
    }

    public void setPoints(int points) {
        Points = points;
    }

    public int getActivation_Status() {
        return Activation_Status;
    }

    public void setActivation_Status(int activation_Status) {
        Activation_Status = activation_Status;
    }

    public String getRegDate() {
        return RegDate;
    }

    public void setRegDate(String regDate) {
        RegDate = regDate;
    }

    public String getLastLoginDate() {
        return LastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        LastLoginDate = lastLoginDate;
    }

    public String getLastLoginTime() {
        return LastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        LastLoginTime = lastLoginTime;
    }

    public String getReserved1() {
        return Reserved1;
    }

    public void setReserved1(String reserved1) {
        Reserved1 = reserved1;
    }

    public String getReserved2() {
        return Reserved2;
    }

    public void setReserved2(String reserved2) {
        Reserved2 = reserved2;
    }

    public String getReserved3() {
        return Reserved3;
    }

    public void setReserved3(String reserved3) {
        Reserved3 = reserved3;
    }
}
