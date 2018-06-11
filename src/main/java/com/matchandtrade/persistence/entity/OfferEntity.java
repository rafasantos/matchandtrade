package com.matchandtrade.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="offer")
public class OfferEntity implements com.matchandtrade.persistence.entity.Entity {

	private Integer offerId;
	private ItemEntity offeredItem;
	private ItemEntity wantedItem;
	
	@OneToOne
	@JoinColumn(name="offered_item_id")
	public ItemEntity getOfferedItem() {
		return offeredItem;
	}

	@Id
	@Column(name="offer_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getOfferId() {
		return offerId;
	}

	@OneToOne
	@JoinColumn(name="wanted_item_id")
	public ItemEntity getWantedItem() {
		return wantedItem;
	}

	public void setOfferedItem(ItemEntity offeredItem) {
		this.offeredItem = offeredItem;
	}

	public void setOfferId(Integer offerId) {
		this.offerId = offerId;
	}

	public void setWantedItem(ItemEntity wantedItem) {
		this.wantedItem = wantedItem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((offeredItem == null) ? 0 : offeredItem.hashCode());
		result = prime * result + ((wantedItem == null) ? 0 : wantedItem.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OfferEntity other = (OfferEntity) obj;
		if (offeredItem == null) {
			if (other.offeredItem != null)
				return false;
		} else if (!offeredItem.equals(other.offeredItem))
			return false;
		if (wantedItem == null) {
			if (other.wantedItem != null)
				return false;
		} else if (!wantedItem.equals(other.wantedItem))
			return false;
		return true;
	}
	
}
