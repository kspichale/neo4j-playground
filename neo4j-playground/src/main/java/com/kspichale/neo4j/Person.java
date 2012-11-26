package com.kspichale.neo4j;

import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.support.index.IndexType;

@NodeEntity
public class Person {

	@GraphId
	private Long id;

	@Indexed(indexName = "peoplesearch", indexType = IndexType.FULLTEXT)
	private String name;

	@RelatedTo(direction = Direction.BOTH, elementClass = Person.class, type = "friends")
	private Set<Person> friends;

	public Person() {
	}

	public Person(String name) {
		this.name = name;
	}

	public void knows(Person friend) {
		friends.add(friend);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
