package com.mycompany.tsms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
 
 
public class SMS {
    // Find your Account Sid and Auth Token at twilio.com/console
 
 
    public SMS(String accountSid, String authToken) {                       
        Twilio.init(accountSid, authToken);
    }
 
 
    public void sendSms(String to, String from, String msg) {
 
        Message message = Message
                .creator(new PhoneNumber(to), 

                        new PhoneNumber(from), 
                        msg)
                .create();
        System.out.println(message.getSid());
    }
}