package atritechnocrat.com.locationchatapplication.pojo;

/**
 * Created by admin on 04-08-2017.
 */

public class Message {

    private String messageId;
    private String userId;
    private String message;

    public Message() {
    }

    public Message(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
