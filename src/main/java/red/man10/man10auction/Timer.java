package red.man10.man10auction;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer {
    private Man10Auction plugin;
    boolean timeauction = false;
    int time;

    int timer;
    public Timer(Man10Auction plugin){
        this.plugin = plugin;
    }
    public void bidTime(){

        time = plugin.config1.getInt("time",60);

        new BukkitRunnable(){
            @Override
            public void run() {
                if(!plugin.auctiontime) {
                	time = 0;
                	cancel();
                	return;
                }

                if (time == 0){
                	if(plugin.auctionp==null) {
                		plugin.auctioncancel();
                		time = 0;
                		cancel();
                		return;
                	}
                    if(plugin.auctionitem.getItemMeta().getDisplayName() != null) {
                        Bukkit.broadcastMessage(plugin.prefix + "§e『"+plugin.auctionitem.getItemMeta().getDisplayName()+"§e』x"+plugin.auctionitem.getAmount()+"§e の競りが時間切れにより終了しました!");
                        }else {
                        Bukkit.broadcastMessage(plugin.prefix + "§e『"+plugin.auctionitem.getType().name()+"§e』x"+plugin.auctionitem.getAmount()+"§e の競りが時間切れにより終了しました!");
                        }
                    plugin.auctionend();
                    time = 0;
                    cancel();
                    return;
                }

                if (time % 60 == 0&&3600 > time){
                	Bukkit.broadcastMessage(plugin.prefix + "§6オークション終了まで残り§e§l" + time/60 + "分");
                }else if ((time % 10 == 0&&60 > time) || (time <= 5&&60 > time) ){
                    Bukkit.broadcastMessage(plugin.prefix + "§6オークション終了まで残り§e§l" + time + "秒");
                }

                time--;

            }
        }.runTaskTimer(plugin,0,20);
    }
    public void bidTime(int times){

        time = times;
        this.timeauction = true;
        new BukkitRunnable(){
            @Override
            public void run() {
                if(!plugin.auctiontime) {
                	time = 0;
                	timeauction = false;
                	cancel();
                	return;
                }

                if (time == 0){
                	if(plugin.auctionp==null) {
                		plugin.auctioncancel();
                		time = 0;
                		timeauction = false;
                		cancel();
                		return;
                	}
                    if(plugin.auctionitem.getItemMeta().getDisplayName() != null) {
                        Bukkit.broadcastMessage(plugin.prefix + "§e『"+plugin.auctionitem.getItemMeta().getDisplayName()+"§e』x"+plugin.auctionitem.getAmount()+"§e の競りが時間切れにより終了しました!");
                        }else {
                        Bukkit.broadcastMessage(plugin.prefix + "§e『"+plugin.auctionitem.getType().name()+"§e』x"+plugin.auctionitem.getAmount()+"§e の競りが時間切れにより終了しました!");
                        }
                    plugin.auctionend();
                    time = 0;
                    timeauction = false;
                    cancel();
                    return;
                }
                if (time % 86400 == 0){
                	Bukkit.broadcastMessage(plugin.prefix + "§6オークション終了まで残り§e§l" + time/86400 + "日");
                }else if (time % 3600 == 0&&86400 > time){
                	Bukkit.broadcastMessage(plugin.prefix + "§6オークション終了まで残り§e§l" + time/3600 + "時間");
                }else if (time % 60 == 0&&3600 > time){
                	Bukkit.broadcastMessage(plugin.prefix + "§6オークション終了まで残り§e§l" + time/60 + "分");
                }else if ((time % 10 == 0&&60 > time) || (time <= 5&&60 > time) ){
                    Bukkit.broadcastMessage(plugin.prefix + "§6オークション終了まで残り§e§l" + time + "秒");
                }else if(time == times) {
                	int zikan = time % 86400;
                	int hun = zikan % 3600;
                	int byou = hun % 60;
                	Bukkit.broadcastMessage(plugin.prefix + "§6オークション終了まで残り§e§l" + time/86400 + "日" + time/3600 + "時間"+ hun/60 + "分"+ byou + "秒");
                }

                time--;

            }
        }.runTaskTimer(plugin,0,20);
    }
}
