/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ibhh.CommandLogger.Commands;

/**
 * 
 * @author ibhh
 */
public class CannotSafeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String mistake;

	// ----------------------------------------------
	// Default constructor - initializes instance variable to unknown
	public CannotSafeException() {
		super(); // call superclass constructor
		mistake = "unknown";
	}

	// -----------------------------------------------
	// Constructor receives some kind of message that is saved in an instance
	// variable.
	public CannotSafeException(String err) {
		super(err); // call super class constructor
		mistake = err; // save message
	}

	// ------------------------------------------------
	// public method, callable by exception catcher. It returns the error
	// message.
	public String getError() {
		return mistake;
	}
}
