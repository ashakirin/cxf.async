package demo.hw.common;

import java.util.ArrayList;
import java.util.List;

public class CallContext {

    private String messageID;
    private String correlationID;
    private String operationName;
    private List<String> relatedMessages = new ArrayList<String>();
    
    
    public CallContext(String messageID, String correlationID, String operationName) {
        super();
        this.messageID = messageID;
        this.correlationID = correlationID;
        this.operationName = operationName;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getCorrelationID() {
        return correlationID;
    }
        
    public void addRelatedMessage(String id) {
        relatedMessages.add(id);
    }
    
    public String getOperationName() {
        return operationName;
    }

    public List<String> getRelatedMessages() {
        return relatedMessages;
    }
}
