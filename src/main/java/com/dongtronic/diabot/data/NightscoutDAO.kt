package com.dongtronic.diabot.data

import com.dongtronic.diabot.util.RedisKeyFormats
import net.dv8tion.jda.core.entities.User
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis
import java.util.*

class NightscoutDAO private constructor() {
    private var jedis: Jedis? = null
    private val logger = LoggerFactory.getLogger(NightscoutDAO::class.java)


    init {
        if (System.getenv("REDIS_URL") != null) {
            jedis = Jedis(System.getenv("REDIS_URL"))
        } else if (System.getenv("DIABOT_REDIS_URL") != null) {
            jedis = Jedis(System.getenv("DIABOT_REDIS_URL"))
        }
    }

    fun getNightscoutUrl(user: User): String? {
        val redisKey = RedisKeyFormats.nightscoutUrlFormat.replace("{{userid}}", user.id)

        return jedis!!.get(redisKey)
    }

    fun setNightscoutUrl(user: User, url: String) {
        val redisKey = RedisKeyFormats.nightscoutUrlFormat.replace("{{userid}}", user.id)

        jedis!!.set(redisKey, url)
    }

    fun removeNIghtscoutUrl(user: User) {
        val redisKey = RedisKeyFormats.nightscoutUrlFormat.replace("{{userid}}", user.id)

        jedis!!.del(redisKey)
    }

    fun listUsers(): TreeMap<String, String> {
        val keys = jedis!!.keys(RedisKeyFormats.allNightscoutUrlsFormat)
        val result = TreeMap<String, String>()

        for(key in keys) {
            val value = jedis!!.get(key)
            result[key] = value
        }

        return result

    }

    companion object {
        private var instance: NightscoutDAO? = null

        fun getInstance(): NightscoutDAO {
            if (instance == null) {
                instance = NightscoutDAO()
            }
            return instance as NightscoutDAO
        }
    }
}
