package com.matchandtrade.persistence.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@Table(name = "user_tb") // 'user' is a reserved word in most databases, hence we are sufixing it with '_tb' 
public class UserEntity implements com.matchandtrade.persistence.entity.Entity {
	
	public enum Role {
		ADMINISTRATOR, USER
	}

	private Set<ArticleEntity> articles = new HashSet<>();
	private Integer userId;
	private String email;
	private String name;
	private Role role = Role.USER;

	@OneToMany
	@JoinTable(name="user_to_article",
		joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name="user_to_article_user_id_fk")),
		inverseJoinColumns = @JoinColumn(name="article_id", foreignKey=@ForeignKey(name="user_to_article_article_id_fk")))
	public Set<ArticleEntity> getArticles() {
		return articles;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserEntity that = (UserEntity) o;
		return Objects.equals(userId, that.userId) &&
			Objects.equals(email, that.email) &&
			Objects.equals(name, that.name) &&
			role == that.role;
	}

	@Column(name = "email", length = 500, nullable = false, unique = true)
	public String getEmail() {
		return email;
	}

	@Column(name = "name", length = 150, nullable = false, unique = false)
	public String getName() {
		return name;
	}

	@Id
	@Column(name = "user_id")
	@SequenceGenerator(name="user_id_generator", sequenceName = "user_id_sequence")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "user_id_generator")
	public Integer getUserId() {
		return userId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, email, name, role);
	}
	
	public void setArticles(Set<ArticleEntity> articles) {
		this.articles = articles;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name="role", nullable=false, unique=false, length=100)
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}
