package xianxian.mc.starocean.globalmarket.messages;

import java.time.LocalDateTime;
import java.util.UUID;

public class Message {
    private int id;
    private UUID from;
    private UUID to;
    private String content;
    private LocalDateTime date;
    private boolean read;

    public Message() {
        this.id = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public UUID getFrom() {
        return from;
    }

    public void setFrom(UUID from) {
        this.from = from;
    }

    public UUID getTo() {
        return to;
    }

    public void setTo(UUID to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public static class Builder {
        private UUID from;
        private UUID to;
        private String content;
        private LocalDateTime date;
        private boolean read;
        
        public Builder from(UUID from) {
            this.from = from;
            return this;
        }
        
        public Builder to(UUID to) {
            this.to = to;
            return this;
        }
        
        public Builder content(String content) {
            this.content = content;
            return this;
        }
        
        public Builder date(LocalDateTime date) {
            this.date = date;
            return this;
        }
        
        public Builder read(boolean read) {
            this.read = read;
            return this;
        }
        
        public Message build() {
            Message message = new Message();
            message.setFrom(from);
            message.setTo(to);
            message.setContent(content);
            message.setDate(date);
            message.setRead(read);
            return message;
        }
    }
}
