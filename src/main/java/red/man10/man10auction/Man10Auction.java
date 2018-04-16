package red.man10.man10auction;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class Man10Auction extends JavaPlugin implements Listener{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("reload")) {
                    getServer().getPluginManager().disablePlugin(this);
                    getServer().getPluginManager().enablePlugin(this);
                    getLogger().info(prefix+"設定を再読み込みしました。");
                    return true;
                }
            }
            getLogger().info(prefix+ChatColor.RED+"ma reload");
            return true;
        }
        Player p = (Player)sender;
        if(!power && !p.hasPermission("man10auction.ignore")) {
        	p.sendMessage(prefix + "§c現在プラグインが無効化されています");
        }
        if(args.length == 0) {
            p.sendMessage(prefix+"§6§l-----§e§lMan10Auction§6§l-----");
            if(auctiontime==true){
                if(auctionitem.getItemMeta().getDisplayName() != null) {
                    p.sendMessage(prefix + "§e現在『"+auctionitem.getItemMeta().getDisplayName()+"§e』x"+auctionitem.getAmount()+"§e が競り中です!");
                    if(auctionp!=null) {
                    p.sendMessage(prefix+"§e現在の最高入札: "+nowmoney+"円 "+auctionp.getName()+"さん");
                    }else {
                    p.sendMessage(prefix+"§e現在の最高入札: "+nowmoney+"円 "+"現在誰も入札していません！");
                    }
                    p.sendMessage(prefix+"§e即決額: "+maxmoney+"円");
                    p.sendMessage(prefix+"§e開催者: "+auctionowner.getName());
                    p.sendMessage(prefix+"§e/ma bid [金額] で入札します");
                }else {
                    p.sendMessage(prefix + "§e現在『§f§l"+auctionitem.getType().name()+"§e』x"+auctionitem.getAmount()+"§e が競り中です!");
                    if(auctionp!=null) {
                    p.sendMessage(prefix+"§e現在の最高入札: "+nowmoney+"円 "+auctionp.getName()+"さん");
                    }else {
                    p.sendMessage(prefix+"§e現在の最高入札: "+nowmoney+"円 "+"現在誰も入札していません！");
                    }
                    p.sendMessage(prefix+"§e即決額: "+maxmoney+"円");
                    p.sendMessage(prefix+"§e開催者: "+auctionowner.getName());
                    p.sendMessage(prefix+"§e/ma bid [金額] で入札します");
                }
            }else{
                p.sendMessage(prefix+"§c現在競り中ではありません！");
                p.sendMessage(prefix+"§e/ma help");
            }
            p.sendMessage(prefix+"§6§l-----§e§l------------§6§l-----");
            return true;
        }else if(args.length == 1) {
        	if(args[0].equalsIgnoreCase("cancel")) {
                if(!p.hasPermission("man10auction.cancel")){
                    p.sendMessage(prefix + "§cあなたにはcancelする権限がありません！");
                    return true;
                }
                if(!auctiontime){
                    p.sendMessage(prefix+"§c現在競り中ではありません！");
                    p.sendMessage(prefix+"§e/ma help");
                    return true;
                }
                auctioncancel();
                return true;
        	}else if(args[0].equalsIgnoreCase("reload")) {
                if(!p.hasPermission("man10auction.reload")){
                    p.sendMessage(prefix + "§cあなたにはreloadする権限がありません！");
                    return true;
                }
                getServer().getPluginManager().disablePlugin(this);
                getServer().getPluginManager().enablePlugin(this);
                p.sendMessage(prefix + "§creloadを行いました。");
                return true;
        	}else if(args[0].equalsIgnoreCase("end")) {
        		if(p!=auctionowner) {
        			p.sendMessage(prefix + "§cあなたはこのオークションのオーナーではありません！");
        			return true;
        		}
        		if(auctionp==null) {
        			p.sendMessage(prefix + "§cまだ誰も入札していないため終了できません！");
        			return true;
        		}
                if(auctionitem.getItemMeta().getDisplayName() != null) {
                Bukkit.broadcastMessage(prefix + "§e『"+auctionitem.getItemMeta().getDisplayName()+"§e』x"+auctionitem.getAmount()+"§e の競りがオーナーにより終了しました!");
                }else {
                Bukkit.broadcastMessage(prefix + "§e『"+auctionitem.getType().name()+"§e』x"+auctionitem.getAmount()+"§e の競りがオーナーにより終了しました!");
                }
                auctionend();
                return true;
        	}else if(args[0].equalsIgnoreCase("help")) {
        		p.sendMessage(prefix+"§6§l-----§e§lMan10Auction§6§l-----");
        		p.sendMessage(prefix+"§e/ma …現在のオークション情報を確認します。");
        		p.sendMessage(prefix+"§e/ma menu …簡単操作の画面を開きます。");
        		p.sendMessage(prefix+"§e/ma new [最低金額] [最大金額] …オークションを開始します。");
        		p.sendMessage(prefix+"§e/ma bid [金額] …オークションに入札します。");
        		p.sendMessage(prefix+"§e/ma end …オークションのオーナーの場合オークションを終了します。");
        		if(p.hasPermission("man10auction.cancel")){
        		p.sendMessage(prefix+"§e/ma cancel …オークションを強制的に終了します。");
        		}
        		if(p.hasPermission("man10auction.reload")){
        		p.sendMessage(prefix+"§e/ma reload …reloadします。");
        		}
        		if(p.hasPermission("man10auction.newtime")){
        		p.sendMessage(prefix+"§e/ma new [start金額] [即決額] [やる時間(秒)] …秒数を指定し開始します。");
        		}
        		if(p.hasPermission("man10auction.on")||p.hasPermission("man10auction.off")){
        		p.sendMessage(prefix+"§e/ma on(off) …プラグインを起動(停止)します");
        		}
        		p.sendMessage(prefix+"§6§l-----§e§l------------§6§l-----");
        		return true;
        	}else if(args[0].equalsIgnoreCase("menu")) {
        		Inventory inv = Bukkit.createInventory(null, 27, prefix);
    			ItemStack items = new ItemStack(Material.GOLD_BLOCK);
    			ItemMeta itemmeta = items.getItemMeta();
    			itemmeta.setDisplayName("§e§l[§6§l入札§e§l]§7(クリック)§r");
    			itemmeta.setUnbreakable(true);
    			itemmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
    			 List<String> k = new ArrayList<String>();
    			 k.add("§a入札の際このボタンを");
    			 k.add("§c押してください！");
    			 if(auctiontime) {
    			  k.add("§e§l§n現在オークション中です！！");
    			 }
    			 itemmeta.setLore(k);
    			items.setItemMeta(itemmeta);
    			ItemStack itemss = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)4);
    			ItemMeta itemmetas = itemss.getItemMeta();
    			itemmetas.setDisplayName("§e§l競り中アイテム§r");
    			itemss.setItemMeta(itemmetas);
    			ItemStack itemsss = new ItemStack(Material.PAPER);
    			ItemMeta itemmetass = itemsss.getItemMeta();
    			itemmetass.setDisplayName("§e§l[§6§l競り開始§e§l]§7(クリック)§r");
    			itemmetass.setUnbreakable(true);
    			itemmetass.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
    			 List<String> kk = new ArrayList<String>();
    			 kk.add("§a開始の際箱のボタンをクリック！");
    			 if(auctiontime) {
    			  kk.add("§c§l§n現在オークション中です！！");
    			 }
    			 itemmetass.setLore(kk);
    			itemsss.setItemMeta(itemmetass);
    			inv.setItem(3, itemss);
    			inv.setItem(4, itemss);
    			inv.setItem(5, itemss);
    			inv.setItem(12, itemss);
    			inv.setItem(14, itemss);
    			inv.setItem(21, itemss);
    			inv.setItem(22, itemss);
    			inv.setItem(23, itemss);
    			inv.setItem(13, auctionitem);
    			inv.setItem(10, items);
    			inv.setItem(16, itemsss);
    			p.openInventory(inv);
    			playerStates.put(p.getUniqueId(), "main");
    			return true;
        	}else if(args[0].equalsIgnoreCase("on")) {
                if(!p.hasPermission("man10auction.on")){
                    p.sendMessage(prefix + "§cあなたにはオークションを起動する権限がありません！");
                    return true;
                }
                power = true;
                p.sendMessage(prefix + "§aオークションプラグインを起動しました。");
                return true;
        	}else if(args[0].equalsIgnoreCase("off")) {
                if(!p.hasPermission("man10auction.off")){
                    p.sendMessage(prefix + "§cあなたにはオークションを停止する権限がありません！");
                    return true;
                }
        		power = false;
        		p.sendMessage(prefix + "§cオークションプラグインを停止しました。");
                if(!auctiontime){
                    return true;
                }
        		auctioncancel();
        		return true;
        	}
        }else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("bid")) {
                if(!auctiontime){
                    p.sendMessage(prefix+"§c現在競り中ではありません！");
                    p.sendMessage(prefix+"§e/ma new [始値] [即売値] …アイテムをもって打つとオークションが始まります");
                    return true;
                }
                if(p==auctionowner){
                    p.sendMessage(prefix+"§c自分の競りに入札はできません!");
                    return true;
                }
                if(p==auctionp){
                    p.sendMessage(prefix+"§cあなたは最高入札者です！");
                    return true;
                }
                int bid = 0;
                try {
                    bid = Integer.parseInt(args[1]);
                    if (nowmoney >= bid) {
                        p.sendMessage(prefix + "§4§l最低金額は" + (nowmoney+1)  + "円です");
                        return true;
                    }
                    if (bid > maxmoney) {
                        p.sendMessage(prefix + "§4§l最高金額は" + maxmoney  + "円です");
                        return true;
                    }

                } catch (NumberFormatException e) {
                    p.sendMessage(prefix + "§4§l金額を指定してください。");
                    return true;

                }
                if(bid > val.getBalance(p.getUniqueId())){
                    p.sendMessage(prefix + "§4§lお金が足りません！");
                    return true;
                }
                val.withdraw(p.getUniqueId(),bid);
                if(auctionp != null){
                    auctionp.sendMessage(prefix+"§cあなたの入札額を上回る入札があったため返金します");
                    val.deposit(auctionp.getUniqueId(),nowmoney);
                }
                auctionp = p;
                nowmoney = bid;
                timer.time = timer.time + 5;
                if(timer.time > 120&&!timer.timeauction) {
                	timer.time = 120;
                }
                if(nowmoney == maxmoney){
                    if(auctionitem.getItemMeta().getDisplayName() != null) {
                        Bukkit.broadcastMessage(prefix + "§e『"+auctionitem.getItemMeta().getDisplayName()+"§e』x"+auctionitem.getAmount()+"§e の競りが即決入札により終了しました!");
                    }else {
                        Bukkit.broadcastMessage(prefix + "§e『"+auctionitem.getType().name()+"§e』x"+auctionitem.getAmount()+"§e の競りが即決入札により終了しました!");
                    }
                    auctionend();
                    return true;
                }
                Bukkit.broadcastMessage(prefix+"§e§l"+p.getName()+"さんが"+bid+"円で入札！時間が+5秒されました: /ma");
                p.sendMessage(prefix + "§e入札が完了しました");
                return true;
            }
        }else if(args.length == 3) {
            if(args[0].equalsIgnoreCase("new")) {
                if(!p.hasPermission("man10auction.new")){
                    p.sendMessage(prefix + "§cあなたには出品する権限がありません！");
                    return true;
                }
                if(auctiontime){
                    p.sendMessage(prefix+"§c現在競り中です！");
                    p.sendMessage(prefix+"§e/ma bid [入札金額] …[入札金額]円で入札します");
                    return true;
                }
                if(p.getInventory().getItemInMainHand().getType()==Material.AIR){
                    p.sendMessage(prefix + "§4§lアイテムを持っていません");
                    return true;
                }
                try {
                    startmoney = Integer.parseInt(args[1]);
                    maxmoney = Integer.parseInt(args[2]);
                    if(0 >= startmoney) {
                        p.sendMessage(prefix + "§4§l始値が0と同じか下回ることはできません");
                        startmoney = 0;
                        maxmoney = 0;
                        return true;
                    }
                    if(startmoney >= maxmoney) {
                        p.sendMessage(prefix + "§4§l即決値が始値と同じか下回ることはできません");
                        startmoney = 0;
                        maxmoney = 0;
                        return true;
                    }
                } catch (NumberFormatException e) {
                    p.sendMessage(prefix + "§4§l金額を指定してください。");
                    return true;

                }
                if(config1.getInt("Commission",1000)>val.getBalance(p.getUniqueId())){
                    p.sendMessage(prefix + "§4手数料分の"+config1.getInt("Commission",1000)+"円を持っていません！");
                    startmoney = 0;
                    maxmoney = 0;
                    return true;
                }
                val.withdraw(p.getUniqueId(),config1.getInt("Commission",1000));
                auctionowner = p;
                auctionitem = p.getInventory().getItemInMainHand();
                p.getInventory().setItemInMainHand(null);
                nowmoney = startmoney;
                auctiontime = true;
                timer.bidTime();
                if(auctionitem.getItemMeta().getDisplayName() != null) {
                    Bukkit.broadcastMessage(prefix + "§6§l"+auctionowner.getName()+"§e§lさんの『"+auctionitem.getItemMeta().getDisplayName()+"§e§l』x"+auctionitem.getAmount()+"§e§l オークションが");
                    Bukkit.broadcastMessage(prefix + "§e§l"+nowmoney+"円から開始されました！即決"+maxmoney+"円: /ma");
                }else {
                    Bukkit.broadcastMessage(prefix + "§6§l"+auctionowner.getName()+"§e§lさんの『"+auctionitem.getType().name()+"§e§l』x"+auctionitem.getAmount()+"§e§l オークションが");
                    Bukkit.broadcastMessage(prefix + "§e§l"+nowmoney+"円から開始されました！即決"+maxmoney+"円: /ma");
                }
                return true;
            }
        }else if(args.length == 4) {
            if(args[0].equalsIgnoreCase("new")) {
                if(!p.hasPermission("man10auction.newtime")){
                    p.sendMessage(prefix + "§cあなたには時間を指定し出品する権限がありません！");
                    return true;
                }
                if(auctiontime){
                    p.sendMessage(prefix+"§c現在競り中です！");
                    p.sendMessage(prefix+"§e/ma bid [入札金額] …[入札金額]円で入札します");
                    return true;
                }
                if(p.getInventory().getItemInMainHand().getType()==Material.AIR){
                    p.sendMessage(prefix + "§4§lアイテムを持っていません");
                    return true;
                }
                int time = 0;
                try {
                    startmoney = Integer.parseInt(args[1]);
                    maxmoney = Integer.parseInt(args[2]);
                    time = Integer.parseInt(args[3]);
                    if(0 >= startmoney) {
                        p.sendMessage(prefix + "§4§l始値が0と同じか下回ることはできません");
                        startmoney = 0;
                        maxmoney = 0;
                        return true;
                    }
                    if(0 >=time) {
                        p.sendMessage(prefix + "§4§l時間が0と同じか下回ることはできません");
                        startmoney = 0;
                        maxmoney = 0;
                        return true;
                    }
                    if(startmoney >= maxmoney) {
                        p.sendMessage(prefix + "§4§l即決値が始値と同じか下回ることはできません");
                        startmoney = 0;
                        maxmoney = 0;
                        return true;
                    }
                } catch (NumberFormatException e) {
                    p.sendMessage(prefix + "§4§l金額を指定してください。");
                    return true;

                }
                if(config1.getInt("Commission",1000)>val.getBalance(p.getUniqueId())){
                    p.sendMessage(prefix + "§4手数料分の"+config1.getInt("Commission",1000)+"円を持っていません！");
                    startmoney = 0;
                    maxmoney = 0;
                    return true;
                }
                val.withdraw(p.getUniqueId(),config1.getInt("Commission",1000));
                auctionowner = p;
                auctionitem = p.getInventory().getItemInMainHand();
                p.getInventory().setItemInMainHand(null);
                nowmoney = startmoney;
                auctiontime = true;
                timer.bidTime(time);
                if(auctionitem.getItemMeta().getDisplayName() != null) {
                    Bukkit.broadcastMessage(prefix + "§6§l"+auctionowner.getName()+"§e§lさんの『"+auctionitem.getItemMeta().getDisplayName()+"§e§l』x"+auctionitem.getAmount()+"§e§l オークションが");
                    Bukkit.broadcastMessage(prefix + "§e§l"+nowmoney+"円から開始されました！即決"+maxmoney+"円: /ma");
                }else {
                    Bukkit.broadcastMessage(prefix + "§6§l"+auctionowner.getName()+"§e§lさんの『"+auctionitem.getType().name()+"§e§l』x"+auctionitem.getAmount()+"§e§l オークションが");
                    Bukkit.broadcastMessage(prefix + "§e§l"+nowmoney+"円から開始されました！即決"+maxmoney+"円: /ma");
                }
                return true;
            }
        }
		p.chat("/ma help");
        return true;
    }
    Player auctionp = null;
    Player auctionowner = null;
    boolean auctiontime = false;
    boolean power = true;
    ItemStack auctionitem = null;
    int maxmoney = 0;
    int startmoney = 0;
    int nowmoney = 0;
    String prefix = "§6§l[§dM§fa§an§f10§eAuction§6§l]§r";
    public FileConfiguration config1;
    VaultManager val = null;
    Timer timer;
    private HashMap<UUID,String> playerStates;
    @Override
    public void onEnable() {
    	getServer().getPluginManager().registerEvents(this, this);
        playerStates = new HashMap<>();
        val = new VaultManager(this);
        timer = new Timer(this);
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        config1 = config;
        getCommand("ma").setExecutor(this);
    }

    @Override
    public void onDisable() {
    	if(auctiontime) {
        auctioncancel();
    	}
    }
    @EventHandler
    public void onExit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(p==auctionowner){
            if(auctionitem.getItemMeta().getDisplayName() != null) {
                Bukkit.broadcastMessage(prefix + "§c『"+auctionitem.getItemMeta().getDisplayName()+"§c』x"+auctionitem.getAmount()+"§c の競りがログアウトにより中止されました!");
            }else {
                Bukkit.broadcastMessage(prefix + "§c『"+auctionitem.getType().name()+"§c』x"+auctionitem.getAmount()+"§c の競りがログアウトにより中止されました!");
            }
            val.deposit(p.getUniqueId(),config1.getInt("Commission",1000));
            auctiontime = false;
            p.getInventory().addItem(auctionitem);
            auctionitem = null;
            maxmoney = 0;
            startmoney = 0;
            if(auctionp!=null) {
                auctionp.sendMessage(prefix + "§cオーナーがログアウトしたため返金します");
                val.deposit(auctionp.getUniqueId(), nowmoney);
                auctionp = null;
            }
            nowmoney = 0;
            return;
        }else if(p==auctionp){
            val.deposit(auctionp.getUniqueId(),nowmoney);
            nowmoney = startmoney;
            Bukkit.broadcastMessage(prefix+"§c最高入札者がログアウトしたため入札をリセットします");
            auctionp = null;
            return;
        }
    }
    @EventHandler
    public void onclick(InventoryClickEvent e) {
        if(playerStates.containsKey(e.getWhoClicked().getUniqueId())) {
    		e.setCancelled(true);
    		if(playerStates.get(e.getWhoClicked().getUniqueId()).equalsIgnoreCase("main")) {
    			if(e.getSlot()==10) {
    				e.getWhoClicked().closeInventory();
                    if(!auctiontime){
                    	e.getWhoClicked().sendMessage(prefix+"§c現在競り中ではありません！");
                    	e.getWhoClicked().sendMessage(prefix+"§e/ma new [始値] [即売値] …アイテムをもって打つとオークションが始まります");
                        return;
                    }
                    Inventory inv = Bukkit.createInventory(null, 9, "入札メニュー");
        			ItemStack items = new ItemStack(Material.BLAZE_POWDER);
        			ItemMeta itemmeta = items.getItemMeta();
        			itemmeta.setDisplayName("§6§l+100円で入札!§7(クリック)§r");
        			 List<String> k = new ArrayList<String>();
        			 k.add("§a現在の最高金額+100円で入札します");
        			 itemmeta.setLore(k);
        			items.setItemMeta(itemmeta);
        			ItemStack itemss = new ItemStack(Material.INK_SACK,1,(short)7);
        			ItemMeta itemmetas = itemss.getItemMeta();
        			itemmetas.setDisplayName("§6§l+10000円で入札!§7(クリック)§r");
        			 List<String> kk = new ArrayList<String>();
        			 kk.add("§a現在の最高金額+10000円で入札します");
        			 itemmetas.setLore(kk);
        			itemss.setItemMeta(itemmetas);
        			ItemStack itemsss = new ItemStack(Material.INK_SACK,1,(short)13);
        			ItemMeta itemmetass = itemsss.getItemMeta();
        			itemmetass.setDisplayName("§6§l+100万円で入札!§7(クリック)§r");
        			 List<String> kkk = new ArrayList<String>();
        			 kkk.add("§a現在の最高金額+100万円で入札します");
        			 itemmetass.setLore(kkk);
        			 itemsss.setItemMeta(itemmetass);
         			ItemStack itemssss = new ItemStack(Material.DIAMOND);
         			ItemMeta itemmetasss = itemssss.getItemMeta();
         			itemmetasss.setDisplayName("§6§l即決金額で入札!§7(クリック)§r");
         			 List<String> kkkk = new ArrayList<String>();
         			 kkkk.add("§a即決金額で入札します");
         			 itemmetasss.setLore(kkkk);
         			 itemssss.setItemMeta(itemmetasss);
        			 inv.setItem(1, items);
        			 inv.setItem(3, itemss);
        			 inv.setItem(5, itemsss);
        			 inv.setItem(7, itemssss);
        			 e.getWhoClicked().openInventory(inv);
        			 playerStates.put(e.getWhoClicked().getUniqueId(), "bid");
                    return;
    			}else if(e.getSlot()==13) {
    				((Player) e.getWhoClicked()).chat("/ma");
    				e.getWhoClicked().closeInventory();
    				return;
    			}else if(e.getSlot()==16) {
                    if(auctiontime){
                    	e.getWhoClicked().closeInventory();
                    	e.getWhoClicked().sendMessage(prefix+"§c現在競り中です！");
                    	e.getWhoClicked().sendMessage(prefix+"§e/ma bid [入札金額] …[入札金額]円で入札します");
                        return;
                    }
                    e.getWhoClicked().sendMessage(prefix+"§e/ma new [始値] [即売値] …アイテムをもって打つとオークションが始まります");
                    e.getWhoClicked().closeInventory();
                    return;
    			}
    		}else if(playerStates.get(e.getWhoClicked().getUniqueId()).equalsIgnoreCase("bid")) {
    			if(e.getSlot()==1) {
    				int bid = nowmoney + 100;
    				((Player) e.getWhoClicked()).chat("/ma bid "+bid);
    				e.getWhoClicked().closeInventory();
    				return;
    			}else if(e.getSlot()==3) {
    				int bid = nowmoney + 10000;
    				((Player) e.getWhoClicked()).chat("/ma bid "+bid);
    				e.getWhoClicked().closeInventory();
    				return;
    			}else if(e.getSlot()==5) {
    				int bid = nowmoney + 1000000;
    				((Player) e.getWhoClicked()).chat("/ma bid "+bid);
    				e.getWhoClicked().closeInventory();
    				return;
    			}else if(e.getSlot()==7) {
    				int bid = maxmoney;
    				((Player) e.getWhoClicked()).chat("/ma bid "+bid);
    				e.getWhoClicked().closeInventory();
    				return;
    			}
    		}
    	}
    }
    @EventHandler
    public void onclose(InventoryCloseEvent e) {
        playerStates.remove(e.getPlayer().getUniqueId());
        return;
    }
    public boolean auctionend(){
    	Bukkit.broadcastMessage(prefix + "§e§l"+auctionp.getName()+"§fさんが落札しました!");
        val.deposit(auctionowner.getUniqueId(),nowmoney);
        auctionowner = null;
        auctiontime = false;
        auctionp.getInventory().addItem(auctionitem);
        auctionp.sendMessage(prefix + "§eおめでとうございます！あなたが落札しました!");
        auctionp = null;
        auctionitem = null;
        maxmoney = 0;
        startmoney = 0;
        nowmoney = 0;
    	return true;
    }
    public boolean auctioncancel(){
        if(auctionitem.getItemMeta().getDisplayName() != null) {
        Bukkit.broadcastMessage(prefix + "§c『"+auctionitem.getItemMeta().getDisplayName()+"§c』x"+auctionitem.getAmount()+"§c の競りが強制キャンセルにより中止されました!");
        }else {
        Bukkit.broadcastMessage(prefix + "§c『"+auctionitem.getType().name()+"§c』x"+auctionitem.getAmount()+"§c の競りが強制キャンセルにより中止されました!");
        }
        val.deposit(auctionowner.getUniqueId(),config1.getInt("Commission",1000));
        auctiontime = false;
        auctionowner.getInventory().addItem(auctionitem);
        auctionitem = null;
        auctionowner = null;
        maxmoney = 0;
        startmoney = 0;
        if(auctionp!=null) {
            auctionp.sendMessage(prefix + "§c強制キャンセルされたため返金します");
            val.deposit(auctionp.getUniqueId(), nowmoney);
            auctionp = null;
        }
        nowmoney = 0;
    	return true;
    }
}
