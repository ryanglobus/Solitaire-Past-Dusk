package com.ryanglobus.solitaire.ui;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ryanglobus.solitaire.game.Rank;
// TODO make more dynamic
// TODO separator UNIX-specific
public class Theme implements Serializable {
	private static final long serialVersionUID = 1289276053399373208L;
	private String name;
	
	public Theme(String name) {
		this.name = name;
	}
	
	public String getFilepath(Rank rank) {
		String prefix = "img/themes/" + name + "/";
		String filepath = prefix + rank.toString().toLowerCase() + ".jpg";
		File f = new File(filepath);
		if (!f.isFile() || !f.canRead()) return null;
		else return filepath;
	}
	
	public static List<Theme> getThemes() {
		List<Theme> themes = new ArrayList<Theme>();
		File themeDir = new File("img/themes"); // TODO MN
		if (themeDir.isDirectory()) {
			for (File child : themeDir.listFiles()) {
				String name = child.getName();
				if (name.startsWith(".")) continue;
				themes.add(new Theme(name));
			}
			Collections.sort(themes, new Comparator<Theme>() {
				@Override
				public int compare(Theme t1, Theme t2) {
					return t1.getName().compareTo(t2.getName());
				}
			});
		} // TODO else what? what if empty?
		return Collections.unmodifiableList(themes);
	}
	
	public String getName() {
		return name;
	}

	public static Theme defaultTheme() {
		List<Theme> themes = getThemes();
		if (themes.isEmpty()) return null;
		else return themes.get(0);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Theme)) {
			return false;
		}
		Theme other = (Theme) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
}
