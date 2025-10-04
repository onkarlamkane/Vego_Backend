package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.math.BigInteger;


/**
 * The persistent class for the chat_messages database table.
 * 
 */
@Entity
@Table(name="chat_messages")
@NamedQuery(name="ChatMessage.findAll", query="SELECT c FROM ChatMessage c")
public class ChatMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="is_read")
	private int isRead;

	@Lob
	private String message;

	@Column(name="receiver_id")
	private BigInteger receiverId;

	@Column(name="sender_id")
	private BigInteger senderId;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	//bi-directional many-to-one association to Chat
	@ManyToOne
	private Chat chat;

	public ChatMessage() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public int getIsRead() {
		return this.isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public BigInteger getReceiverId() {
		return this.receiverId;
	}

	public void setReceiverId(BigInteger receiverId) {
		this.receiverId = receiverId;
	}

	public BigInteger getSenderId() {
		return this.senderId;
	}

	public void setSenderId(BigInteger senderId) {
		this.senderId = senderId;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Chat getChat() {
		return this.chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

}