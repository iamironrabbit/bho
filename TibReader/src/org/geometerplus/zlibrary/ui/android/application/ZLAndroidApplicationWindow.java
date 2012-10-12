/*
 * Copyright (C) 2007-2012 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.zlibrary.ui.android.application;

import java.util.*;
import java.io.*;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.application.ZLApplicationWindow;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.core.view.ZLViewWidget;

import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;
import org.geometerplus.zlibrary.ui.android.error.ErrorKeys;

import org.geometerplus.android.util.UIUtil;
import org.ironrabbit.bho.BhoMenu;
import org.ironrabbit.bho.BhoMenu.BhoMenuItem;
import org.ironrabbit.bho.BhoMenu.BhoSubMenu;
import org.ironrabbit.bho.BhoMenu.OnMenuItemClickListener;

public final class ZLAndroidApplicationWindow extends ZLApplicationWindow {
	private final HashMap<BhoMenuItem,String> myMenuItemMap = new HashMap<BhoMenuItem,String>();

	private final OnMenuItemClickListener myMenuListener =
		new OnMenuItemClickListener() {
			public boolean onMenuItemClick(BhoMenuItem item) {
				getApplication().runAction(myMenuItemMap.get(item));
				return true;
			}
		};

	public ZLAndroidApplicationWindow(ZLApplication application) {
		super(application);
	}

	public BhoSubMenu addSubMenu(BhoMenu menu, String id) {
		return menu.addSubMenu(ZLResource.resource("menu").getResource(id).getValue());
	}

	// TODO: THIS.
	public void addMenuItem(BhoSubMenu menu, String actionId, Integer iconId, String name) {
		if (name == null) {
			name = ZLResource.resource("menu").getResource(actionId).getValue();
		}
		menu.getParent().menuInContext = menu.getParent().subMenu;
		final BhoMenuItem menuItem = menu.getParent().add(name);
		if (iconId != null) {
			menuItem.setIcon(iconId);
		}
		menuItem.setOnMenuItemClickListener(myMenuListener);
		myMenuItemMap.put(menuItem, actionId);
	}
	
	public void addMenuItem(BhoMenu menu, String actionId, Integer iconId, String name) {
		if (name == null) {
			name = ZLResource.resource("menu").getResource(actionId).getValue();
		}
		final BhoMenuItem menuItem = menu.add(name);
		if (iconId != null) {
			menuItem.setIcon(iconId);
		}
		menuItem.setOnMenuItemClickListener(myMenuListener);
		myMenuItemMap.put(menuItem, actionId);
	}

	@Override
	public void refresh() {
		for (Map.Entry<BhoMenuItem,String> entry : myMenuItemMap.entrySet()) {
			final String actionId = entry.getValue();
			final ZLApplication application = getApplication();
			final BhoMenuItem menuItem = entry.getKey();
			menuItem.setVisible(application.isActionVisible(actionId) && application.isActionEnabled(actionId));
			switch (application.isActionChecked(actionId)) {
				case B3_TRUE:
					menuItem.setCheckable(true);
					menuItem.setChecked(true);
					break;
				case B3_FALSE:
					menuItem.setCheckable(true);
					menuItem.setChecked(false);
					break;
				case B3_UNDEFINED:
					menuItem.setCheckable(false);
					break;
			}
		}
	}
	
	@Override
	public void runWithMessage(String key, Runnable action, Runnable postAction) {
		final Activity activity = 
			((ZLAndroidLibrary)ZLAndroidLibrary.Instance()).getActivity();
		if (activity != null) {
			UIUtil.runWithMessage(activity, key, action, postAction, false);
		} else {
			action.run();
		}
	}

	@Override
	protected void processException(Exception exception) {
		exception.printStackTrace();

		final Activity activity = 
			((ZLAndroidLibrary)ZLAndroidLibrary.Instance()).getActivity();
		final Intent intent = new Intent(
			"android.fbreader.action.ERROR",
			new Uri.Builder().scheme(exception.getClass().getSimpleName()).build()
		);
		intent.putExtra(ErrorKeys.MESSAGE, exception.getMessage());
		final StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		intent.putExtra(ErrorKeys.STACKTRACE, stackTrace.toString());
		/*
		if (exception instanceof BookReadingException) {
			final ZLFile file = ((BookReadingException)exception).File;
			if (file != null) {
				intent.putExtra("file", file.getPath());
			}
		}
		*/
		try {
			activity.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			// ignore
			e.printStackTrace();
		}
	}

	@Override
	public void setTitle(final String title) {
		final Activity activity = 
			((ZLAndroidLibrary)ZLAndroidLibrary.Instance()).getActivity();
		if (activity != null) {
			activity.runOnUiThread(new Runnable() {
				public void run() {
					activity.setTitle(title);
				}
			});
		}
	}

	protected ZLViewWidget getViewWidget() {
		return ((ZLAndroidLibrary)ZLAndroidLibrary.Instance()).getWidget();
	}

	public void close() {
		((ZLAndroidLibrary)ZLAndroidLibrary.Instance()).finish();
	}

	private int myBatteryLevel;
	protected int getBatteryLevel() {
		return myBatteryLevel;
	}
	public void setBatteryLevel(int percent) {
		myBatteryLevel = percent;
	}
}
