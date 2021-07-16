package chat.messaging;

public class Chat {
    private String sender;
    private String reciever;
    private String message;
    private String fileUri;
    private String imageUri;


    public Chat(String sender, String reciever, String message,String fileUri,String imageUri) {
        this.sender = sender;
        this.reciever = reciever;
        this.message = message;
        this.fileUri=fileUri;
        this.imageUri=imageUri;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public String getReciever() {
        return reciever;
    }

    public String getMessage() {
        return message;
    }
    public String getFileUri() {
        return fileUri;
    }
    public String getImageUri() {
        return imageUri;
    }


    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }


}
