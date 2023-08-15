import java.text.SimpleDateFormat;
import java.util.Date;

public class EventMessage {

    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    int userid;
    int sessionId;
    int value;
    Date eventTime;

    public EventMessage(int userid, int sessionId, Date eventTime, int value) {

        super();
        this.userid = userid;
        this.sessionId = sessionId;
        this.eventTime = eventTime;
        this.value = value;
    }

    public EventMessage(String commaDelimitedMessage) throws Exception {

        String[] elements = commaDelimitedMessage.split(",");

        if (elements.length != 4) {
            throw new Exception("Must have 4 fields");
        }

        userid = Integer.parseInt(elements[0]);
        sessionId = Integer.parseInt(elements[1]);
        eventTime = sdfDate.parse(elements[2]);
        value = Integer.parseInt(elements[3]);

    }

    public String toCsvString() {

        StringBuilder builder = new StringBuilder();
        builder.append(userid);
        builder.append(",");
        builder.append(sessionId);
        builder.append(",");
        builder.append(sdfDate.format(eventTime));
        builder.append(",");
        builder.append(value);

        return builder.toString();
    }

   
    /**
     * @return the userid
     */
    public int getUserid() {
        return userid;
    }

}
