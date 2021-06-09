package com.wzp.usercenter.service.user;

import com.wzp.usercenter.dao.bonus_event_log.BonusEventLogMapper;
import com.wzp.usercenter.dao.user.UserMapper;
import com.wzp.usercenter.domain.dto.messaging.UserAddBonusMsgDTO;
import com.wzp.usercenter.domain.dto.user.UserLoginDTO;
import com.wzp.usercenter.domain.entity.bonus_event_log.BonusEventLog;
import com.wzp.usercenter.domain.entity.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class UserService
{
    private final UserMapper userMapper;
    private final BonusEventLogMapper bonusEventLogMapper;

    public User findById(Integer id)
    {
        return userMapper.selectByPrimaryKey(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addBonus(UserAddBonusMsgDTO userAddBonusMsgDTO)
    {
        //1.为用户加积分
        User user = this.userMapper.selectByPrimaryKey(userAddBonusMsgDTO.getUserId());
        Integer bonus = userAddBonusMsgDTO.getBonus();
        user.setBonus(user.getBonus()+bonus);
        this.userMapper.updateByPrimaryKeySelective(user);
        //2.记录日志到bonus_event_log
        bonusEventLogMapper.insert(
                BonusEventLog.builder()
                        .userId(user.getId())
                        .createTime(new Date())
                        .description(userAddBonusMsgDTO.getDescription())
                        .event(userAddBonusMsgDTO.getEvent())
                        .value(bonus)
                        .build()
        );
        log.info("积分添加完毕");
    }

    /**
     * 获取微信登录的用户信息，若已注册直接返回，不存在插入后返回
     * @param userLoginDTO
     * @param openId
     * @return
     */
    public User login(UserLoginDTO userLoginDTO, String openId)
    {
        User user = this.userMapper.selectOne(
                User.builder()
                        .wxId(openId)
                        .build()
        );
        if(user == null)
        {
            User userToSave = User.builder()
                    .wxId(openId)
                    .bonus(300)
                    .wxNickname(userLoginDTO.getWxNickname())
                    .avatarUrl(userLoginDTO.getAvatarUrl())
                    .roles("user")
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            this.userMapper.insertSelective(userToSave);
            return userToSave;
        }

        return user;
    }
}
