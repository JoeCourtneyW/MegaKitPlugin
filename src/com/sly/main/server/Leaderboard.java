package com.sly.main.server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import com.sly.main.Server;
import com.sly.main.player.PlayerModel;
import com.sly.main.resources.LocationUtil;
import com.sly.main.resources.MathUtil;

public class Leaderboard
{

	private String columnName;
	private int size;
	private PlayerModel[] top;

	public Leaderboard(String columnName, int size) {
		this.columnName = columnName;
		this.size = MathUtil.limitValue(size, 2, 100);
		top = new PlayerModel[size];
		update();
	}

	public String getColumnName() {
		return this.columnName;
	}

	public int getSize() {
		return this.size;
	}

	public PlayerModel[] getTop() {
		return top;
	}

	private Comparator<PlayerModel> comparator() {
		return new Comparator<PlayerModel>() {
			@Override
			public int compare(PlayerModel p1, PlayerModel p2) {
				return (int) (p1.getDatabaseValue(columnName).asDouble() - p2.getDatabaseValue(columnName).asDouble());
			}
		};
	}

	public void update() {
		List<PlayerModel> players = new ArrayList<PlayerModel>(Server.getAllPlayers());
		players.sort(comparator());
		players.subList(0, top.length);
		top = players.toArray(new PlayerModel[top.length]);

	}

	public void postToSigns(Location firstSign) {
		Location l = firstSign.clone();
		if (!(l.getWorld().getBlockAt(l).getState() instanceof Sign))
			return;
		Sign sign = (Sign) l.getWorld().getBlockAt(l).getState();
		int height = 1;
		int width = 2; // Default is width of 2
		if (size % 5 == 0)
			height = 5;
		else if (size % 4 == 0)
			height = 4;
		else if (size % 3 == 0)
			height = 3;
		width = (int) (size / (height * 1D));
		for (int y = 0; y < height; y++) {
			if (!(sign.getWorld().getBlockAt(l.clone().subtract(0D, y, 0D)) instanceof Sign))
				break; // Make sure the next block will be a sign
			sign = (Sign) sign.getWorld().getBlockAt(l.clone().subtract(0D, y, 0D)); // Reset next sign to next row
			for (int x = 0; x < width; x++) {
				int place = ((3 * y) + (x + 1));
				sign.setLine(0, "#" + place);
				sign.setLine(1, "§b§l" + top[place - 1].getDatabaseValue("PlayerName"));
				sign.setLine(2, "§1" + columnName + ":");
				sign.setLine(3, "§l" + top[place - 1].getDatabaseValue(columnName));

				sign.update();
				if (!(sign.getWorld().getBlockAt(LocationUtil.getRelativeBlockToTheRight(sign)) instanceof Sign))
					break; // Make sure the next block will be a sign
				sign = (Sign) sign.getWorld().getBlockAt(LocationUtil.getRelativeBlockToTheRight(sign)).getState();

			}
		}
	}
}
