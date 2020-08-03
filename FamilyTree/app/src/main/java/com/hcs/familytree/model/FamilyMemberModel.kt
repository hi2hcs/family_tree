package com.hcs.familytree.model

/**
 * 家族树数据模型
 * */
data class FamilyMemberModel(var memberEntity: FamilyMemberEntity) {
    companion object {
        val MOST_LEFT_FLAG = 1
        val MOST_TOP_FLAG = 2
        val MOST_RIGHT_FLAG = 4
    }

    var parentIndex = 0
    var spouseIndex = 0

    // 配偶
    var spouseEntity: FamilyMemberEntity? = null

    // 孩子
    var childModels: List<FamilyMemberModel>? = null
    var parentId: String? = null

    // 当前模型所处的代数
    var level = 0
    var mostLeft = false

    //顶上中间
    var toParentX = 0
    var toParentY = 0

    //底下中间or侧边中间
    var toChildX = 0
    var toChildY = 0

    var startCenterX = 0

    var flag = 0
    var isSpouse = false

    override fun toString(): String {
        return "{memberEntity=$memberEntity}"
    }


}