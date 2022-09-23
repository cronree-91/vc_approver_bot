package jp.cron.sample.data.repo;

import jp.cron.sample.data.entity.VoiceChannelEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoiceChannelRepository extends MongoRepository<VoiceChannelEntity, String> {
}
