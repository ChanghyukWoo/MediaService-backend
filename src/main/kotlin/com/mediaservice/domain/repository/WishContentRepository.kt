package com.mediaservice.domain.repository

import com.mediaservice.domain.WishContent
import com.mediaservice.domain.WishContentEntity
import com.mediaservice.domain.WishContentTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class WishContentRepository {
    fun findByProfileId(id: UUID): List<WishContent> {
        return WishContentEntity.find {
            WishContentTable.profile eq id and (WishContentTable.isDeleted eq false)
        }.map { WishContent.from(it) }
    }

    fun save(wishContent: WishContent): WishContent {
        wishContent.id = WishContentTable.insertAndGetId {
            it[mediaContents] = wishContent.mediaContents.id
            it[profile] = wishContent.profile.id
            it[isDeleted] = false
        }.value

        return wishContent
    }

    fun existsByProfileIdAndMediaAllSeriesId(profileId: UUID, mediaAllSeriesId: UUID): Boolean {
        return !WishContentEntity.find {
            WishContentTable.profile eq profileId and (WishContentTable.mediaContents eq mediaAllSeriesId) and (WishContentTable.isDeleted eq false)
        }.empty()
    }

    fun delete(wishContent: WishContent): WishContent {
        val wishContentEntity = WishContentEntity.find {
            WishContentTable.profile eq wishContent.profile.id and (WishContentTable.mediaContents eq wishContent.mediaContents.id) and (WishContentTable.isDeleted eq false)
        }.first()

        wishContentEntity.isDeleted = true

        return WishContent.from(wishContentEntity)
    }
}
