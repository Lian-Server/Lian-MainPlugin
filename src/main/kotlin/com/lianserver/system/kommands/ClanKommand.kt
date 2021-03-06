package com.lianserver.system.kommands

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.lianserver.system.common.Clan
import com.lianserver.system.common.Country
import com.lianserver.system.common.LianPlayer
import com.lianserver.system.interfaces.KommandInterface
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import org.bukkit.Material
import org.bukkit.block.Skull
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import kotlin.reflect.typeOf

/***
 * @author AlphaGot
 */

class ClanKommand: KommandInterface {
    override fun kommand() {
        getInstance().kommand {
            register("clan") {
                then("help"){
                    executes {
                        sender.sendMessage(
                                    "${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}- 클랜 도움말 ${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-\n" +
                                    "${ChatColor.RESET}${clanTextS("${ChatColor.WHITE} * /clan info: 클랜 정보를 보여줍니다.")}\n" +
                                    "${clanTextS("${ChatColor.WHITE} * /clan create <클랜 이름>: <클랜 이름> 클랜을 생성합니다.")}\n" +
                                    "${clanTextS("${ChatColor.WHITE} * /clan invite <플레이어>: <플레이어>를 초대합니다.")}\n" +
                                    "${clanTextS("${ChatColor.WHITE} * /clan accept: 대기 중인 클랜 가입 요청을 수락합니다.")}\n" +
                                    "${clanTextS("${ChatColor.WHITE} * /clan all: 모든 클랜을 보여줍니다.")}\n" +
                                    "${clanTextS("${ChatColor.WHITE} * /clan chat: 클랜 채팅 모드를 전환합니다.")}\n" +
                                    "${clanTextS("${ChatColor.WHITE} * /clan leave: 클랜을 나갑니다.")}\n" +
                                    "${clanTextS("${ChatColor.WHITE} * /clan kick <플레이어>: <플레이어>를 추방합니다.")}\n" +
                                    "${clanTextS("${ChatColor.WHITE} * /clan public: 공개된 클랜 목록을 보여줍니다.")}\n" +
                                    "${clanTextS("${ChatColor.WHITE} * /clan togglepublic: 클랜의 공개 상태를 전환합니다.")}\n"
                        )
                    }
                }
                then("create") {
                    executes { sender.sendMessage(clanText("클랜 이름을 입력해주세요.")) }
                    then("clanName" to string(StringType.GREEDY_PHRASE)) {
                        executes { kommandContext ->
                            val hand = (sender as Player).inventory.itemInMainHand
                            if(hand.type != Material.ENCHANTED_BOOK){
                                sender.sendMessage(clanText("손에 클랜 창설권을 들어주세요."))
                            }
                            else {
                                if(hand.itemMeta.hasDisplayName()){
                                    if (hand.itemMeta.displayName() != text("클랜 창설권", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)) {
                                        sender.sendMessage(clanText("손에 클랜 창설권을 들어주세요."))
                                    }
                                    else{
                                        val clanName: String by kommandContext

                                        var clanNameTemp = clanName

                                        clanNameTemp = clanNameTemp.replace("§", " ")
                                        clanNameTemp = clanNameTemp.replace("&", " ")
                                        clanNameTemp = clanNameTemp.replace("_", " ")
                                        if(getInstance().getPlayer(sender).clan != null){
                                            sender.sendMessage(clanText("이미 클랜에 소속되어있습니다."))
                                        }
                                        else if(getInstance().getPlayer(sender).country != null){
                                            sender.sendMessage(clanText("이미 국가에 소속되어있습니다."))
                                        }
                                        else {
                                            if(getInstance().clans.none { it.value.name == clanNameTemp }) {
                                                val clan = Clan(getInstance().onlinePlayers[(sender as Player).uniqueId.toString()]!!, null, name = clanNameTemp)
                                                getInstance().onlinePlayers[(sender as Player).uniqueId.toString()]!!.clan = clan
                                                getInstance().clans[(sender as Player).uniqueId.toString()] = clan
                                                hand.subtract(1)

                                                getInstance().server.broadcast(clanText("${sender.name}님이 ${ChatColor.GREEN}${clan.name}${ChatColor.WHITE}클랜을 생성했습니다."))
                                            }
                                            else {
                                                sender.sendMessage(clanText("이미 같은 이름의 클랜이 있습니다."))
                                            }
                                        }
                                    }
                                }
                                else{
                                    sender.sendMessage(clanText("손에 클랜 창설권을 들어주세요."))
                                }
                            }
                        }
                    }
                }
                executes { sender.sendMessage(clanText("/clan help")) }
                then("info") {
                    then("clanName" to string(StringType.GREEDY_PHRASE)){
                        executes {
                            val clanName: String by it

                            if(getInstance().clans.any { it.value.name == clanName }){
                                val clan = getInstance().clans.values.first { it.name == clanName }

                                sender.sendMessage(clanText("클랜 정보"))
                                sender.sendMessage("${ChatColor.WHITE}<${ChatColor.YELLOW}${clan.name} 클랜${ChatColor.WHITE}>")
                                clan.players.forEach {
                                    sender.sendMessage("${ChatColor.YELLOW}${it.player.name}")
                                }
                            }
                            else {
                                sender.sendMessage(clanText("해당 이름을 가진 클랜이 없습니다."))
                            }
                        }
                    }
                    executes {
                        if(getInstance().getPlayer(sender).clan != null){
                            sender.sendMessage(clanText("클랜 정보"))
                            sender.sendMessage("${ChatColor.WHITE}<${ChatColor.YELLOW}${getInstance().getPlayer(sender).clan!!.name} 클랜${ChatColor.WHITE}>")
                            getInstance().getPlayer(sender).clan!!.players.forEach {
                                sender.sendMessage("${ChatColor.YELLOW}${it.player.name}")
                            }
                            if(getInstance().getPlayer(sender).clan!!.land != null){
                                sender.sendMessage(clanText("땅 좌표: ${getInstance().getPlayer(sender).clan!!.land}"))
                            }
                        }
                        else {
                            sender.sendMessage(clanText("클랜에 소속되어있지 않습니다!"))
                        }
                    }
                }
                then("invite"){
                    executes {
                        sender.sendMessage(clanText("누구를 초대할 지를 입력해주세요."))
                    }
                    then("target" to player()){
                        executes {
                            if (getInstance().getPlayer(sender).clan == null) {
                                sender.sendMessage(clanText("클랜에 소속되어있지 않습니다!"))
                            }
                            else {
                                if (getInstance().getPlayer(sender).clan!!.owner != getInstance().getPlayer(sender)) {
                                    sender.sendMessage(clanText("클랜장만 초대할 수 있습니다."))
                                } else {
                                    if(getInstance().getPlayer(sender).clan!!.players.size < 4){
                                        val target: Player by it

                                        if(getInstance().onlinePlayers.containsKey(target.uniqueId.toString())){
                                            getInstance().invites[target.uniqueId.toString()] = getInstance().getPlayer(sender).clan!!
                                            sender.sendMessage(clanText("초대장을 보냈습니다."))
                                            if(getInstance().getPlayer(target).player.isOnline){
                                                val p = getInstance().server.onlinePlayers.first { it.uniqueId == getInstance().getPlayer(target).player.uniqueId }
                                                p.sendMessage(clanText("${sender.name}님이 클랜 초대장을 보냈습니다. (${getInstance().getPlayer(sender).clan!!.name} 클랜)"))
                                                val clickComp = net.md_5.bungee.api.chat.TextComponent("[ 수락 ]")
                                                clickComp.color = ChatColor.GREEN
                                                clickComp.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder("클랜 수락하기").create())
                                                clickComp.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan accept")
                                                p.sendMessage(clickComp)

                                                getInstance().invitesTaskId[target.uniqueId.toString()] = getInstance().server.scheduler.scheduleSyncDelayedTask(getInstance(),
                                                    {
                                                        getInstance().invites.remove(target.uniqueId.toString())
                                                        target.sendMessage(clanText("${sender.name}님에게서 온 클랜 초대가 만료되었습니다."))
                                                    },
                                                    600L
                                                )
                                            }
                                        }
                                    }
                                    else {
                                        player.sendMessage(clanText("클랜 인원은 클랜장 포함 4명입니다."))
                                    }
                                }
                            }
                        }
                    }
                }
                then("accept"){
                    executes {
                        val p = getInstance().getPlayer(player)
                        if(getInstance().invites.containsKey(p.player.uniqueId.toString())){
                            if(getInstance().invites[p.player.uniqueId.toString()]!!.players.size < 4){
                                if(p.clan == null && p.country == null){
                                    p.clan = getInstance().invites[p.player.uniqueId.toString()]
                                    getInstance().onlinePlayers[p.player.uniqueId.toString()] = p
                                    getInstance().clans[getInstance().invites[p.player.uniqueId.toString()]!!.owner.player.uniqueId.toString()]!!.players =
                                        getInstance().clans[getInstance().invites[p.player.uniqueId.toString()]!!.owner.player.uniqueId.toString()]!!.players.plusElement(p) as MutableList<LianPlayer>
                                    getInstance().clans[getInstance().invites[p.player.uniqueId.toString()]!!.owner.player.uniqueId.toString()]!!.players.forEach { pl ->
                                        if(pl.player.isOnline){
                                            val p2 = getInstance().server.onlinePlayers.first { it.uniqueId == pl.player.uniqueId }
                                            p2.sendMessage("${p.player.name}님이 클랜에 가입했습니다. 환영 인사 한 번씩 해주세요!")
                                        }
                                    }
                                    player.sendMessage("${getInstance().clans[getInstance().invites[p.player.uniqueId.toString()]!!.owner.player.uniqueId.toString()]!!.name} 클랜에 가입했습니다.")
                                    getInstance().invites.remove(player.uniqueId.toString())
                                    getInstance().server.scheduler.cancelTask(getInstance().invitesTaskId[player.uniqueId.toString()]!!)
                                    getInstance().invitesTaskId.remove(player.uniqueId.toString())
                                }
                                else {
                                    sender.sendMessage(clanText("이미 다른 클랜/국가에 속해있습니다."))
                                }
                            }
                            else {
                                player.sendMessage(clanText("클랜 인원은 클랜장 포함 4명입니다."))
                            }
                        }
                        else {
                            player.sendMessage(clanText("대기 중인 초대가 없습니다."))
                        }
                    }
                }
                then("leave"){
                    executes {
                        if(getInstance().getPlayer(player).clan == null){
                            player.sendMessage(clanText("나갈 클랜이 없습니다."))
                        }
                        else {
                            if(getInstance().getPlayer(player).clan!!.owner.player.uniqueId == player.uniqueId){
                                player.sendMessage(clanText("현재 클랜의 클랜장입니다. 진짜로 클랜을 삭제하시려면 /clan leave deleteclan 명령어를 입력해주세요."))
                            }
                            else {
                                getInstance().getPlayer(player).clan!!.players.forEach {pl ->
                                    if(pl.player.isOnline){
                                        (getInstance().server.onlinePlayers.first { it.uniqueId == pl.player.uniqueId }).sendMessage(clanText("${player.name}님이 클랜에서 나가셨습니다."))
                                    }
                                }
                                getInstance().clans[getInstance().getPlayer(player).clan!!.owner.player.uniqueId.toString()]!!.players.remove(getInstance().getPlayer(player))
                                player.sendMessage(clanText("클랜에서 나오셨습니다."))
                                getInstance().onlinePlayers[player.uniqueId.toString()]!!.clanChatMode = false
                                getInstance().onlinePlayers[player.uniqueId.toString()]!!.clan = null
                            }
                        }
                    }
                    then("delete"){
                        executes {
                            if(getInstance().getPlayer(sender).clan != null){
                                if(getInstance().getPlayer(sender).clan!!.owner.player.uniqueId.toString() == (sender as Player).uniqueId.toString()){
                                    getInstance().server.getWorld("world")!!.entities.forEach { if(it.type == EntityType.ARMOR_STAND && it.scoreboardTags.contains("#lian_flag") && it.scoreboardTags.contains("${player.uniqueId}")) it.remove() }
                                    getInstance().clans.remove(getInstance().getPlayer(player).player.uniqueId.toString())
                                    getInstance().getPlayer(player).clan!!.players.forEach { pl ->
                                        getInstance().onlinePlayers[pl.player.uniqueId.toString()]!!.clanChatMode = false
                                        getInstance().onlinePlayers[pl.player.uniqueId.toString()]!!.clan = null
                                        if(pl.player.isOnline){
                                            (getInstance().server.onlinePlayers.first { it.uniqueId == pl.player.uniqueId }).sendMessage(clanText("클랜이 해체되었습니다."))
                                        }
                                    }
                                }
                                else {
                                    sender.sendMessage(clanText("클랜장이 아닙니다."))
                                }
                            }
                            else {
                                sender.sendMessage(clanText("클랜에 소속되어있지 않습니다!"))
                            }
                        }
                    }
                }
                then("all"){
                    executes {
                        sender.sendMessage(clanText("모든 클랜: "))
                        getInstance().clans.forEach {
                            sender.sendMessage("${ChatColor.YELLOW}${it.value.name} 클랜")
                        }
                    }
                }
                then("chat"){
                    executes {
                        if(getInstance().getPlayer(player).clan != null){
                            getInstance().onlinePlayers[getInstance().getPlayer(player).player.uniqueId.toString()]!!.clanChatMode = !getInstance().onlinePlayers[getInstance().getPlayer(player).player.uniqueId.toString()]!!.clanChatMode
                            sender.sendMessage(clanText("${
                                if(!getInstance().getPlayer(player).clanChatMode) "전체 채팅"
                                else "클랜 채팅"
                            }을 사용합니다."))
                        }
                        else {
                            sender.sendMessage(clanText("클랜에 소속되어있지 않습니다!"))
                        }
                    }
                }
                then("kick"){
                    executes {
                        sender.sendMessage("누구를 추방할 것인지 입력해주세요.")
                    }
                    then("victim" to player()){
                        executes {
                            val victim: Player by it

                            if(getInstance().onlinePlayers.containsKey(victim.uniqueId.toString())){
								if(getInstance().getPlayer(player).clan != null){
									if(getInstance().getPlayer(player).clan!!.owner.player.uniqueId == player.uniqueId){
										if(getInstance().getPlayer(player).clan!!.owner.player.uniqueId != victim.uniqueId){
                                            getInstance().getPlayer(player).clan!!.players.first {
                                                it.player.uniqueId == victim.uniqueId
                                            }.clan = null
                                            getInstance().getPlayer(player).clan!!.players.first {
                                                it.player.uniqueId == victim.uniqueId
                                            }.clanChatMode = false

                                            victim.sendMessage(clanText("클랜에서 추방되었습니다."))
                                            getInstance().getPlayer(player).clan!!.players.forEach {pl ->
                                                if(pl.player.isOnline){
                                                    (getInstance().server.onlinePlayers.first { it.uniqueId == pl.player.uniqueId }).sendMessage(clanText("${victim.name}님이 클랜에서 추방되셨습니다."))
                                                }
                                            }
                                            getInstance().getPlayer(player).clan!!.players.remove(getInstance().getPlayer(player).clan!!.players.first {
                                                it.player.uniqueId == victim.uniqueId
                                            })
                                            getInstance().clans[player.uniqueId.toString()] = getInstance().getPlayer(player).clan!!

                                            player.sendMessage(clanText("${victim.name}님을 클랜에서 추방했습니다."))
                                        }
                                        else {
                                            sender.sendMessage(clanText("현재 클랜의 클랜장입니다. 클랜을 삭제하려면 /clan leave delete 명령어를 써주세요."))
                                        }
									}
									else {
										sender.sendMessage(clanText("클랜장만 추방할 수 있습니다."))
									}
								}
							}
                        }
                    }
                }
                then("upgrade"){
                    executes {
                        if(getInstance().getPlayer(sender).clan == null){
                            sender.sendMessage(clanText("클랜이 없습니다."))
                        }
                        else if(getInstance().getPlayer(sender).clan!!.owner.player.uniqueId != getInstance().getPlayer(sender).player.uniqueId){
                            sender.sendMessage(clanText("클랜장이 아닙니다."))
                        }
                        else {
                            val hand = (sender as Player).inventory.itemInMainHand
                            if(hand.type != Material.ENCHANTED_BOOK){
                                sender.sendMessage(clanText("손에 국가 창설권을 들어주세요."))
                            }
                            else {
                                if(hand.itemMeta.hasDisplayName()){
                                    if (hand.itemMeta.displayName() != text("국가 창설권", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)) {
                                        sender.sendMessage(clanText("손에 국가 창설권을 들어주세요."))
                                    }
                                    else{
                                        if(getInstance().getPlayer(sender).clan!!.land == null){
                                            sender.sendMessage(clanText("클랜의 땅이 있어야 합니다."))
                                        }
                                        else {
                                            val country = Country(getInstance().getPlayer(sender), getInstance().getPlayer(sender).clan!!.land, getInstance().getPlayer(sender).clan!!.players, getInstance().getPlayer(sender).clan!!.name, 0)
                                            getInstance().onlinePlayers[(sender as Player).uniqueId.toString()]!!.clan = null
                                            getInstance().onlinePlayers[(sender as Player).uniqueId.toString()]!!.country = country
                                            getInstance().countries[(sender as Player).uniqueId.toString()] = country

                                            country.players.forEach {
                                                getInstance().onlinePlayers[it.player.uniqueId.toString()]!!.clan = null
                                                getInstance().onlinePlayers[it.player.uniqueId.toString()]!!.country = country
                                            }

                                            getInstance().invites.filterValues { it.owner.player.uniqueId == player.uniqueId }.forEach {
                                                getInstance().invites.remove(it.key)
                                                getInstance().invitesTaskId.remove(it.key)
                                            }
                                            getInstance().clans.remove((sender as Player).uniqueId.toString())

                                            getInstance().server.broadcast(countryText("${sender.name}님이 ${ChatColor.GOLD}${country.name}${ChatColor.WHITE} 국가를 생성했습니다."))
                                            hand.subtract(1)
                                        }
                                    }
                                }
                                else{
                                    sender.sendMessage(clanText("손에 국가 창설권을 들어주세요."))
                                }
                            }
                        }
                    }
                }
                then("public"){
                    executes {
                        val gui = ChestGui(6, "공개 클랜")

                        val pages = PaginatedPane(0, 0, 9, 5)
                        pages.populateWithItemStacks(
                            getInstance().clans.values.filter {
                                it.public && it.players.size < 4
                            }.shuffled().map {
                                val st = ItemStack(Material.PLAYER_HEAD)
                                val meta = st.itemMeta as SkullMeta

                                val c = it

                                meta.owningPlayer = it.owner.player
                                meta.displayName(text("${it.name} 클랜").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false))
                                meta.lore(listOf(
                                    text("클랜장: ").color(TextColor.color(0xE0, 0xE0, 0x66)).decoration(TextDecoration.ITALIC, false).append(
                                        if(getInstance().server.onlinePlayers.any { it.uniqueId == meta.owningPlayer!!.uniqueId }){
                                            getInstance().server.onlinePlayers.first { it.uniqueId == meta.owningPlayer!!.uniqueId }.displayName().decoration(TextDecoration.ITALIC, false)
                                        }
                                        else{
                                            text(it.owner.player.name!!).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
                                        }
                                    ),
                                    text("현재 접속자 수: ${getInstance().server.onlinePlayers.count { 
                                        c.players.map { it.player.uniqueId }.contains(it.uniqueId)
                                    }}명").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                                ))

                                st.itemMeta = meta

                                st
                            }
                        )
                        pages.setOnClick { e: InventoryClickEvent ->
                            e.isCancelled = true

                            if(e.currentItem != null) {
                                val mm = e.currentItem!!.itemMeta
                                getInstance().logger.info(e.currentItem!!.itemMeta::class.java.toString())
                                val cc = (mm as SkullMeta).owningPlayer
                                val p = getInstance().getPlayer(e.whoClicked)

                                if(getInstance().clans[cc!!.uniqueId.toString()]!!.players.size < 4){
                                    if(p.clan == null && p.country == null){
                                        p.clan = getInstance().clans[cc.uniqueId.toString()]
                                        getInstance().onlinePlayers[p.player.uniqueId.toString()] = p
                                        getInstance().clans[cc.uniqueId.toString()]!!.players =
                                            getInstance().clans[cc.uniqueId.toString()]!!.players.plusElement(p) as MutableList<LianPlayer>
                                        getInstance().clans[cc.uniqueId.toString()]!!.players.forEach { pl ->
                                            if(pl.player.isOnline){
                                                val p2 = getInstance().server.onlinePlayers.first { it.uniqueId == pl.player.uniqueId }
                                                p2.sendMessage("${p.player.name}님이 클랜에 가입했습니다. 환영 인사 한 번씩 해주세요!")
                                            }
                                        }
                                        player.sendMessage("${getInstance().clans[cc.uniqueId.toString()]!!.name} 클랜에 가입했습니다.")
                                    }
                                    else {
                                        sender.sendMessage(clanText("이미 다른 클랜/국가에 속해있습니다."))
                                    }
                                }
                                else {
                                    player.sendMessage(clanText("클랜 인원은 클랜장 포함 4명입니다."))
                                }
                            }
                        }

                        gui.addPane(pages)

                        val background = OutlinePane(0, 5, 9, 1)
                        background.addItem(GuiItem(ItemStack(Material.BLACK_STAINED_GLASS_PANE)))
                        background.setRepeat(true)
                        background.priority = Pane.Priority.LOWEST
                        background.setOnClick { event: InventoryClickEvent ->
                            event.isCancelled = true
                        }

                        gui.addPane(background)

                        val rw = ItemStack(Material.RED_WOOL)
                        var meta = rw.itemMeta
                        meta.displayName(text("이전").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                        rw.itemMeta = meta

                        val navigation = StaticPane(0, 5, 9, 1)
                        navigation.addItem(
                            GuiItem(
                            rw
                        ) { event: InventoryClickEvent ->
                            event.isCancelled = true
                            if (pages.page > 0) {
                                pages.page = pages.page
                                gui.update()
                            }
                        }, 0, 0
                        )

                        val gw = ItemStack(Material.GREEN_WOOL)
                        meta = gw.itemMeta
                        meta.displayName(text("다음").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                        gw.itemMeta = meta

                        navigation.addItem(
                            GuiItem(
                            gw
                        ) { event: InventoryClickEvent ->
                            event.isCancelled = true
                            if (pages.page < pages.pages) {
                                pages.page = pages.page + 1
                                gui.update()
                            }
                        }, 8, 0
                        )

                        val br = ItemStack(Material.BARRIER)
                        meta = br.itemMeta
                        meta.displayName(text("닫기").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false))
                        br.itemMeta = meta

                        navigation.addItem(
                            GuiItem(
                            br
                        ) { event: InventoryClickEvent ->
                            event.isCancelled = true
                            event.whoClicked.closeInventory()
                        }, 4, 0
                        )

                        gui.addPane(navigation)

                        gui.update()
                        gui.show((sender as Player))
                    }
                }
                then("togglepublic"){
                    executes {
                        if(getInstance().getPlayer(sender).clan == null){
                            sender.sendMessage(clanText("클랜에 소속되어있지 않습니다!"))
                        }
                        else {
                            val clan = getInstance().getPlayer(sender).clan!!

                            if(clan.owner.player.uniqueId != (sender as Player).uniqueId){
                                sender.sendMessage(clanText("클랜장이 아닙니다."))
                            }
                            else {
                                getInstance().clans[(sender as Player).uniqueId.toString()]!!.public = !getInstance().clans[(sender as Player).uniqueId.toString()]!!.public
                                sender.sendMessage(clanText("공개 상태를 ${
                                    if(getInstance().clans[(sender as Player).uniqueId.toString()]!!.public) "공개" else "비공개"
                                }로 설정했습니다."))
                            }
                        }
                    }
                }
            }
        }
    }

    init {}
}