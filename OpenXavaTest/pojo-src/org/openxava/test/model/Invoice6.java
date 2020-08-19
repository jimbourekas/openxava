package org.openxava.test.model;


import java.math.*;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@IdClass(InvoiceKey.class)  // We reuse the key class for Invoice
@Table(name="INVOICE")
@View(members="year; number; date; amountsSum")
@View(name="WithDetails", members="year; number; date; details; amountsSum")
public class Invoice6 {
	
	@Id @Column(length=4) @Max(9999l) @Required
	@DefaultValueCalculator(CurrentYearCalculator.class)
	private int year;
	
	@Id @Column(length=6) @Required
	private int number;
		
	@Required
	@DefaultValueCalculator(CurrentLocalDateCalculator.class)
	private java.time.LocalDate date;
	
	@OneToMany(mappedBy="invoice") // With no Cascade, for testing a case
	private Collection<InvoiceDetail6> details;
	
	@Stereotype("MONEY") 
	private BigDecimal amountsSum;   
		
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public java.time.LocalDate getDate() {
		return date;
	}

	public void setDate(java.time.LocalDate date) {
		this.date = date;
	}

	public BigDecimal getAmountsSum() {
		return amountsSum;
	}

	public void setAmountsSum(BigDecimal amountsSum) {
		this.amountsSum = amountsSum;
	}

	public Collection<InvoiceDetail6> getDetails() {
		return details;
	}

	public void setDetails(Collection<InvoiceDetail6> details) {
		this.details = details;
	}

}
