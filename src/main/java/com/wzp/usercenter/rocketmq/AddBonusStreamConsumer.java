package com.wzp.usercenter.rocketmq;

import com.alibaba.fastjson.JSON;
import com.wzp.usercenter.dao.bonus_event_log.BonusEventLogMapper;
import com.wzp.usercenter.dao.user.UserMapper;
import com.wzp.usercenter.domain.dto.messaging.UserAddBonusMsgDTO;
import com.wzp.usercenter.domain.entity.bonus_event_log.BonusEventLog;
import com.wzp.usercenter.domain.entity.user.User;
import com.wzp.usercenter.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddBonusStreamConsumer
{
    private final UserService userService;

    @StreamListener(Sink.INPUT)
    public void receive(String message)
    {
        UserAddBonusMsgDTO userAddBonusMsgDto  = JSON.parseObject(message, UserAddBonusMsgDTO.class);
        userAddBonusMsgDto.setDescription("投稿加积分");
        userAddBonusMsgDto.setEvent("CONTRIBUTE");
        this.userService.addBonus(userAddBonusMsgDto);
    }

}
