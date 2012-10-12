package org.ironrabbit.bho;

import java.util.ArrayList;
import java.util.List;

import org.geometerplus.zlibrary.ui.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.ironrabbit.bho.BhoMenuFactory.BhoMenuBuilder;


public class BhoMenu {
	Activity a;
	LayoutInflater li;
	PopupWindow pw;
	AlertDialog ad;
	Display display;
	
	View inner;
	TableLayout table;
	
	boolean isShowing = false;	
	BhoMenu menu;
	public BhoMenuFactory menuInContext;
	
	public BhoMainMenu mainMenu = null;
	public BhoSubMenu subMenu = null;
	BhoMoreMenu moreMenu = null;
	
	public interface OnMenuItemClickListener {
		public boolean onMenuItemClick(BhoMenuItem bmi);
	}
	
	public BhoMenu(Activity a) {
		this.a = a;
		li = LayoutInflater.from(a);
		
		display = ((WindowManager) a.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		mainMenu = new BhoMainMenu();
		menuInContext = mainMenu;
		
		inner = li.inflate(R.layout.bho_menu, null);
		table = (TableLayout) inner.findViewById(R.id.bho_menu_holder);
		
		menu = this;
	}
	
	public synchronized BhoMenuItem add(int index, String label) {
		if(!(menuInContext instanceof BhoSubMenu)) {
			if(menuInContext.items.size() >= (menuInContext.itemsPerRow * menuInContext.maxRows) - 1) {
				if(moreMenu == null) {
					moreMenu = new BhoMoreMenu();
					BhoMenuItem moreItem = new BhoMenuItem(-1, "More", mainMenu);
					moreItem.setIcon(android.R.drawable.ic_dialog_info);
					moreItem.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							hide();
							show(v, moreMenu);
						}
					});
					menuInContext.items.add(moreItem);
					menuInContext = moreMenu;
				} else 
					menuInContext = moreMenu;
			}
		}
		
		BhoMenuItem bmi = new BhoMenuItem(index, label);
		Log.d(BhoTyper.BHOTAG, "hello " + index + " " + label + " adding to " + menuInContext.getClass().getName());
		menuInContext.items.add(bmi);
		return bmi;
	}
	
	public synchronized BhoMenuItem add(String label) {
		int index = menuInContext.items.size() == 0 ? 0 : menuInContext.items.size();
		return add(index, label);
	}
	
	public BhoSubMenu addSubMenu(String value) {
		BhoMenuItem subMenuItem = menu.add(value);
		subMenuItem.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hide();
				show(v, subMenu);
			}
		});
		subMenu = new BhoSubMenu(value);
		return subMenu;
	}
	
	public synchronized void show(View v) {
		if(menu.isShowing) {
			menu.hide();
			return;
		}
		
		show(v, mainMenu);
	}
		
	public synchronized void show(View v, BhoMenuFactory thisMenu) {		
		if(thisMenu.items.size() < 1)
			return;
		
		menu.isShowing = true;
		((BhoMenuBuilder) thisMenu).show(v);
	}
	
	public synchronized void hide() {
		menu.isShowing = false;
		if(pw != null) {
			pw.dismiss();
			pw = null;
		}
		return;
	}
	
	public class BhoMenuItem extends LinearLayout {
		int id;
		BhoTextView label;
		ImageView icon;
		boolean isVisible, isChecked, isCheckable = false;
		BhoMenuFactory menuRoot;
		
		public BhoMenuItem(int id, String label, BhoMenuFactory menuRoot) {
			super(a);
			
			this.id = id;
			this.menuRoot = menuRoot;
			
			if(!(menuRoot instanceof BhoSubMenu)) {
				this.addView(li.inflate(R.layout.bho_menuitem, null));			
				icon = (ImageView) findViewById(R.id.bho_menuitem_icon);
			} else {
				this.addView(li.inflate(R.layout.bho_menuitem_submenu, null));
			}
			this.label = (BhoTextView) findViewById(R.id.bho_menuitem_label);
			this.label.setText(label);
		}
		
		public BhoMenuItem(int id, String label) {
			this(id, label, menuInContext);
		}

		public int getItemId() {
			return id;
		}

		public void setIcon(int iconId) {
			icon.setImageResource(iconId);
		}
		
		public void setOnMenuItemClickListener(final OnMenuItemClickListener omicl) {
			this.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					hide();
					omicl.onMenuItemClick(BhoMenuItem.this);
					
				}
			});
		}

		public void setVisible(boolean b) {
			isVisible = b;
		}

		public void setChecked(boolean b) {
			isChecked = b;
			
		}

		public void setCheckable(boolean b) {
			isCheckable = b;
			
		}
	}
	
	private class BhoMainMenu extends BhoMenuFactory implements BhoMenuBuilder {
		BhoMainMenu() {
			super(a);
			
			itemsPerRow = 3;
			maxRows = 2;
		}
		
		@Override
		public void show(View v) {
			pw = new PopupWindow(inner, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, false);
			pw.setAnimationStyle(android.R.style.Animation_Dialog);
			pw.setWidth(display.getWidth());
			
			build();
			table.removeAllViews();
			for(TableRow tr : rows)
				table.addView(tr);
			
			pw.showAtLocation(v, Gravity.BOTTOM, 0, 0);
				
		}
	}
	
	public class BhoSubMenu extends BhoMenuFactory implements BhoMenuBuilder {
		String label;
		
		BhoSubMenu(String label) {
			super(a);
			this.label = label;
			maxRows = 1;
			itemsPerRow = 1;
			
		}
		
		@Override
		public void show(View v) {
			pw = new PopupWindow(inner, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, false);
			pw.setAnimationStyle(android.R.style.Animation_Dialog);
			pw.setWidth(display.getWidth());
			
			build();
			table.removeAllViews();
			for(TableRow tr : rows)
				table.addView(tr);
			
			pw.showAtLocation(v, Gravity.BOTTOM, 0, 0);
		}
		
		public BhoMenu getParent() {
			return menu;
		}
		
	}
	
	private class BhoMoreMenu extends BhoMenuFactory implements BhoMenuBuilder {
		BhoMoreMenu() {
			super(a);
			
			itemsPerRow = 3;
			
		}
		
		@Override
		public void show(View v) {
			maxRows = items.size()/itemsPerRow;
			pw = new PopupWindow(inner, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, false);
			pw.setAnimationStyle(android.R.style.Animation_Dialog);
			pw.setWidth(display.getWidth());
			
			
			build();
			table.removeAllViews();
			for(TableRow tr : rows)
				table.addView(tr);
			
			pw.showAtLocation(v, Gravity.BOTTOM, 0, 0);
		}
		
	}
}
