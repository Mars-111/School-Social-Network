package ru.kors.chatsservice.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReleaseMediaJWTService {

    @Value("${media-service.release.jwt.secret}")
    String secret;




    

}

