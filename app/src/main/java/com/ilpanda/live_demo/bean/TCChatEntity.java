package com.ilpanda.live_demo.bean;


/**
 * Module:   TCChatEntity
 * <p>
 * Function: 消息载体类。
 */
public class TCChatEntity {
    private String grpSendName;    // 发送者的名字
    private String content;        // 消息内容
    private int type;            // 消息类型
    private String accountId;   // 发送者 Id

    public String getSenderName() {
        return grpSendName != null ? grpSendName : "";
    }

    public void setSenderName(String grpSendName) {
        this.grpSendName = grpSendName;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String context) {
        this.content = context;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TCChatEntity)) return false;

        TCChatEntity that = (TCChatEntity) o;

        if (getType() != that.getType()) return false;
        if (grpSendName != null ? !grpSendName.equals(that.grpSendName) : that.grpSendName != null)
            return false;
        return getContent() != null ? getContent().equals(that.getContent()) : that.getContent() == null;

    }

    @Override
    public int hashCode() {
        int result = grpSendName != null ? grpSendName.hashCode() : 0;
        result = 31 * result + (getContent() != null ? getContent().hashCode() : 0);
        result = 31 * result + getType();
        return result;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
