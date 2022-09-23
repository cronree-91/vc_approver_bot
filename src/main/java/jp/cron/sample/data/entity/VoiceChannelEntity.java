package jp.cron.sample.data.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class VoiceChannelEntity {
    @Id
    public String id;

    public String name;

}
