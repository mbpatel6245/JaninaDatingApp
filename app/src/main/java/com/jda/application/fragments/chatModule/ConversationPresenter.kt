package com.jda.application.fragments.chatModule

interface ConversationPresenter {
    fun apiGetChat(param: Map<String, Any>, isPagination: Boolean)
    fun apiChatRating(param: ChatRatingRequest)
    fun apiMeetRating(param: MeetRatingRequest)
    fun apiRejectChatRating(param: ChatRatingRejectRequest)
//    fun apiBlockUnBlockUser(data: BlockUnBlockRequestModel)
//    fun apiClearChat(data: ClearChatRequestModel)
}