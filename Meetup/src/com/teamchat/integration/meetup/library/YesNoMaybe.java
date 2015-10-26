
package com.teamchat.integration.meetup.library;

public enum YesNoMaybe
{
	YES, NO, MAYBE;
	
	public String getLowerCaseName()
	{
		return this.name().toLowerCase();
	}
}
