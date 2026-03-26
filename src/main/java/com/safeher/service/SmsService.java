package com.safeher.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    private final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
    private final String FROM_PHONE = System.getenv("TWILIO_PHONE");

    public void sendSOS(String toPhone, String message) {

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message.creator(
                new PhoneNumber(toPhone),
                new PhoneNumber(FROM_PHONE),
                message
        ).create();
    }
}
