package in.co.codeplanet.urlshortner.Controller;

import in.co.codeplanet.urlshortner.bean.EmailDetails;
import in.co.codeplanet.urlshortner.bean.userDetails;
import in.co.codeplanet.urlshortner.service.EmailService;
import in.co.codeplanet.urlshortner.utility.Otp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

@RestController
public class urlController {

    @Autowired
    private EmailService emailService;

    @PostMapping("register")
    public String signUp(@RequestBody userDetails userDetails) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/urlManager", "root", "root");) {
            String query1 = "select * from user where username=? or email=?";
            PreparedStatement stmt = con.prepareStatement(query1);
            stmt.setString(1, userDetails.getUserName());
            stmt.setString(2, userDetails.getEmail());
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return "this username or email exits";
            else {

                int otp = Integer.parseInt(Otp.generateOtp(4));
                EmailDetails emailDetails = new EmailDetails(userDetails.getEmail(), "otp verification", "your otp is " + otp);
                emailService.sendMail(emailDetails);
                String query = "insert into user(username, email, password,otp, is_verified) values(?,?,?,?,?)";
                PreparedStatement stmt1 = con.prepareStatement(query);
                stmt1.setString(1, userDetails.getUserName());
                stmt1.setString(2, userDetails.getEmail());
                stmt1.setString(3, userDetails.getPassword());
                stmt1.setInt(4, otp);
                stmt1.setInt(5, 0);
                stmt1.executeUpdate();
                return "account registered successfully";
            }
        } catch (Exception e) {
            return "something went wrong";
        }
    }

    @PostMapping("otpVerification")
    public String verify(@RequestBody userDetails userDetails) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/urlManager", "root", "root");) {
            String query = "select otp from user where email=?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, userDetails.getEmail());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt(1) == userDetails.getOtp()) {
                    String query1 = "update user set is_verified=1 where email=?";
                    PreparedStatement stmt1 = con.prepareStatement(query1);
                    stmt1.setString(1, userDetails.getEmail());
                    stmt1.executeUpdate();
                    return "your email has been successfully verified";
                } else {
                    return "otp doesnt match";
                }
            } else {
                return "there is no account corresponding to this email";
            }
        } catch (Exception e) {
            return "something went wrong";
        }

    }

    @PostMapping("login")
    public String login(@RequestBody userDetails userDetails) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/urlManager", "root", "root");) {
            String query = "select * from user where email=? and password=? and is_verified=1";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, userDetails.getEmail());
            stmt.setString(2, userDetails.getPassword());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return "login successful";
            } else {
                return "invalid email or password or your account has not been verified";
            }
        } catch (Exception e) {

            return "something went wrong, try after sometime";
        }
    }

    @GetMapping("forgotPassword")
    public String forgotPassword(@RequestParam String userName) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/urlManager", "root", "root");) {
            String query = "select email from user where username=?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String email = rs.getString(1);
                String password = Otp.generateOtp(8);
                EmailDetails emailDetails = new EmailDetails(email, "new password", "your new password  is " + password);
                emailService.sendMail(emailDetails);
                String query1 = "update user set password=? where username=?";
                PreparedStatement stmt1 = con.prepareStatement(query1);
                stmt1.setString(1, password);
                stmt1.setString(2, userName);
                stmt1.executeUpdate();
                return "new password has been sent over your mail id";

            } else {
                return "username doesnt exist";
            }

        } catch (Exception e) {
            return " something went wrong";
        }
    }

    @PostMapping("changePassword")
    public String changePassword(@RequestBody userDetails userDetails) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/urlManager", "root", "root");) {
            String query = "select * from user where email=? and password=?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, userDetails.getEmail());
            stmt.setString(2, userDetails.getPassword());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String query1 = "update user set password=? where email=?";
                PreparedStatement stmt1 = con.prepareStatement(query1);
                stmt1.setString(1, userDetails.getNewPassword());
                stmt1.setString(2, userDetails.getEmail());
                stmt1.executeUpdate();
                return "password has been updates successfully";

            } else {
                return "incorrect email or password";
            }

        } catch (Exception e) {
            return "";
        }


    }

    @GetMapping("shorten")
    public String shortener(@RequestParam String long_url, String short_url, Integer user_id) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/urlManager", "root", "root");) {
            String query1 = "select * from url where short_url=?";
            PreparedStatement stmt1 = con.prepareStatement(query1);
            stmt1.setString(1, "cpt.cc/" + short_url);
            ResultSet rs = stmt1.executeQuery();
            if (rs.next())
                return "this custom hash is already in use";
            else {
                if (user_id == null)
                    user_id = 0;
                String query = "insert into url values(?,?,?)";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, long_url);
                stmt.setString(2, "cpt.cc/" + short_url);
                stmt.setInt(3, user_id);
                stmt.executeUpdate();
                return "url has been shortened";
            }
        } catch (Exception e) {
            return "something went wrong";
        }

    }

    @GetMapping("longUrl")
    public String longUrl(@RequestParam String short_url) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/urlManager", "root", "root");) {
            String query1 = "select long_url from urdetail where short_url=?";
            PreparedStatement stmt1 = con.prepareStatement(query1);
            stmt1.setString(1, "cpt.cc/" + short_url);
            ResultSet rs = stmt1.executeQuery();
            if (rs.next())
                return " your long url is " + rs.getString(1);
            else
                return "this short url is not linked to any long url";
        } catch (Exception e) {
            return "this short url is not linked with any long url";
        }

    }

    @GetMapping("allUrl")
    public  HashMap<String,String> allUrl(@RequestParam int user_id) {
        try (Connection con =DriverManager.getConnection("jdbc:mysql://localhost:3306/urlManager", "root", "root");) {
            String query1 = "select long_url, short_url  from urdetail where user_id=?";
            PreparedStatement stmt1 = con.prepareStatement(query1);
            stmt1.setInt(1,user_id);
            ResultSet rs=stmt1.executeQuery();
            HashMap<String,String> hm=new HashMap<String,String>();
            while(rs.next())
            {
              hm.put(rs.getString(2),rs.getString(1));
            }
            return hm;

        } catch (Exception e) {
  return null;
        }
    }
}





