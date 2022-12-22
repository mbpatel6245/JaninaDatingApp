package com.jda.application.fragments.messagesModule

interface ChatPresenter {
    fun apiGetChatList(param :Map<String, Any>,isPaginationCall:Boolean)
    fun apiDeleteUser(data:DeleteUserRequestModel)
}