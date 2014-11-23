package com.logic;

import java.util.ArrayList;
import java.util.List;

public class DataTableObject {

	int  recordsTotal;

	int  recordsFiltered;

	String  sEcho;

	String sColumns;

	ArrayList data;
	
	int draw;

	public String getsEcho() {
		return sEcho;
	}

	public void setsEcho(String sEcho) {
		this.sEcho = sEcho;
	}

	public String getsColumns() {
		return sColumns;
	}

	public void setsColumns(String sColumns) {
		this.sColumns = sColumns;
	}

	public ArrayList getAaData() {
		return data;
	}

	public void setAaData(ArrayList aaData) {
		this.data = aaData;
	}

	public int getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(int recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public int getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(int recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}



}