package com.perfect.filesystem.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;


/**
 * The persistent class for the dailyreport database table.
 * 
 */
@Entity
public class Dailyreport implements Serializable {

	private static final long serialVersionUID = -2863633036597087595L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Long id;

	private String curdate;

	private int files;

	private int groups;

	private int spaces;

	public Dailyreport() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCurdate() {
		return this.curdate;
	}

	public void setCurdate(String curdate) {
		this.curdate = curdate;
	}

	public int getFiles() {
		return this.files;
	}

	public void setFiles(int files) {
		this.files = files;
	}

	public int getGroups() {
		return this.groups;
	}

	public void setGroups(int groups) {
		this.groups = groups;
	}

	public int getSpaces() {
		return this.spaces;
	}

	public void setSpaces(int spaces) {
		this.spaces = spaces;
	}

}