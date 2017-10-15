package de.ceruti.mackie;

import de.ceruti.curcuma.api.keyvaluecoding.exceptions.ValidationException;
import de.ceruti.curcuma.core.utils.RegistrationTool;
import de.ceruti.curcuma.foundation.NSObjectImpl;
import de.ceruti.curcuma.keyvalueobserving.PostKVONotifications;

public class RegistrationModel extends NSObjectImpl  {

	public RegistrationModel() {
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public String getCustomer() {
		return customer;
	}

	@PostKVONotifications
	public void setCustomer(String customer) {
		this.customer = customer;
//		verify();
	}

	public String getRegistrationKey() {
		return registrationKey;
	}

	@PostKVONotifications
	public void setRegistrationKey(String registrationKey) {
		this.registrationKey = registrationKey;
//		verify();
	}
	
	public String validateCustomer(String s) throws ValidationException {
		return s==null ? "" : s.trim();
	}
	
	public String validateRegistrationKey(String s) throws ValidationException{
		return s==null ? "" : s.trim();
	}

	private String customer;
	private String registrationKey;
	
	
	public boolean isRegistered() {
		return verify();
	}

	
	private static final String salt =  "MackieLCDView";
	
	private boolean verify(){
		//FREE Software
		return true;
//		return RegistrationTool.verify(customer, salt, registrationKey);
	}
	
	public static void main(String[] args) {
		System.out.println(RegistrationTool.genAuthcode(args[0],salt));
	}
}

