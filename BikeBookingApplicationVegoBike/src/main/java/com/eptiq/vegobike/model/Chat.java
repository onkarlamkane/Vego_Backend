package com.eptiq.vegobike.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.math.BigInteger;
import java.util.List;


/**
 * The persistent class for the chats database table.
 * 
 */
@Entity
@Table(name="chats")
@NamedQuery(name="Chat.findAll", query="SELECT c FROM Chat c")
public class Chat implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="bike_id")
	private int bikeId;

	@Column(name="buyer_id")
	private BigInteger buyerId;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="seller_id")
	private BigInteger sellerId;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	//bi-directional many-to-one association to ChatMessage
	@OneToMany(mappedBy="chat")
	private List<ChatMessage> chatMessages;

	public Chat() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getBikeId() {
		return this.bikeId;
	}

	public void setBikeId(int bikeId) {
		this.bikeId = bikeId;
	}

	public BigInteger getBuyerId() {
		return this.buyerId;
	}

	public void setBuyerId(BigInteger buyerId) {
		this.buyerId = buyerId;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public BigInteger getSellerId() {
		return this.sellerId;
	}

	public void setSellerId(BigInteger sellerId) {
		this.sellerId = sellerId;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<ChatMessage> getChatMessages() {
		return this.chatMessages;
	}

	public void setChatMessages(List<ChatMessage> chatMessages) {
		this.chatMessages = chatMessages;
	}

	public ChatMessage addChatMessage(ChatMessage chatMessage) {
		getChatMessages().add(chatMessage);
		chatMessage.setChat(this);

		return chatMessage;
	}

	public ChatMessage removeChatMessage(ChatMessage chatMessage) {
		getChatMessages().remove(chatMessage);
		chatMessage.setChat(null);

		return chatMessage;
	}

}