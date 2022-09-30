package com.easemob.agora.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "APP_USER_INFO")
public class AppUserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_account")
    private String userAccount;

    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "agora_chat_user_name")
    private String agoraChatUserName;

    @Column(name = "agora_chat_user_uuid")
    private String agoraChatUserUuid;

}
