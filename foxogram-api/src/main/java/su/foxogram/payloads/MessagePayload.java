package su.foxogram.payloads;

import java.util.List;

public class MessagePayload {
	private String content;
	private List<String> attachments;

	public List<String> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
