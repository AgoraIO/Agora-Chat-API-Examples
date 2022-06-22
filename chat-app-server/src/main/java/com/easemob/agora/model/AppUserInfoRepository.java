package com.easemob.agora.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserInfoRepository extends JpaRepository<AppUserInfo, Long> {

    @Query(value = "select * from app_user_info where user_account=?", nativeQuery = true)
    AppUserInfo findByUserAccount(String userAccount);

    @Query(value = "select * from app_user_info where agora_uid=?", nativeQuery = true)
    AppUserInfo findByAgoraUid(String agoraUid);
}
