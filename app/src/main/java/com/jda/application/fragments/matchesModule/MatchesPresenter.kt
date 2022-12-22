package com.jda.application.fragments.matchesModule

interface MatchesPresenter {
    fun apiGetMatchesRequestList(param :Map<String, Any>,isPaginationCall:Boolean)
}