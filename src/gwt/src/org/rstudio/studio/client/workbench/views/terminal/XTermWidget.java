/*
 * XTermWidget.java
 *
 * Copyright (C) 2009-16 by RStudio, Inc.
 *
 * Unless you have received this program directly from RStudio pursuant
 * to the terms of a commercial license agreement with RStudio, then
 * this program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */

package org.rstudio.studio.client.workbench.views.terminal;

import org.rstudio.core.client.CommandWithArg;
import org.rstudio.core.client.ElementIds;
import org.rstudio.core.client.ExternalJavaScriptLoader;
import org.rstudio.core.client.ExternalJavaScriptLoader.Callback;
import org.rstudio.core.client.resources.StaticDataResource;
import org.rstudio.studio.client.common.SuperDevMode;
import org.rstudio.studio.client.workbench.views.console.ConsoleResources;
import org.rstudio.studio.client.workbench.views.terminal.xterm.XTermDimensions;
import org.rstudio.studio.client.workbench.views.terminal.xterm.XTermNative;
import org.rstudio.studio.client.workbench.views.terminal.xterm.XTermResources;
import org.rstudio.studio.client.workbench.views.terminal.xterm.XTermThemeResources;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class XTermWidget extends Widget implements RequiresResize
{
   public XTermWidget()
   {
      styles_ = ConsoleResources.INSTANCE.consoleStyles();
      createContainerElement();
      terminal_ = XTermNative.createTerminal(true);
      terminal_.open(getElement());
   }

   private void showBanner()
   {
      terminal_.writeln("Welcome to RStudio shell.");
      terminal_.writeln("Now brought to you by XTerm.");
      
      XTermDimensions screenSize = terminal_.proposeGeometry();
      String msg = new String("Screen size=");
      msg += screenSize.getCols();
      msg += "x";
      msg += screenSize.getRows();
      terminal_.writeln(msg);
   }
   
   private void createContainerElement()
   {
      attachTheme(XTermThemeResources.INSTANCE.xtermcss());
      setElement(Document.get().createDivElement());
      getElement().setTabIndex(0);
      getElement().getStyle().setMargin(0, Unit.PX);
      getElement().getStyle().setBackgroundColor("#111");
      getElement().getStyle().setColor("#fafafa");
   }
   
   private void attachTheme(StaticDataResource cssResource)
   {
      if (currentStyleEl_ != null)
         currentStyleEl_.removeFromParent();

      currentStyleEl_ = Document.get().createLinkElement();
      currentStyleEl_.setType("text/css");
      currentStyleEl_.setRel("stylesheet");
      currentStyleEl_.setHref(cssResource.getSafeUri().asString());
      Document.get().getBody().appendChild(currentStyleEl_);
   }
  
   private boolean initialized_ = false;
   @Override
   protected void onLoad()
   {
      super.onLoad();
      if (!initialized_)
      {
         initialized_ = true;
         Scheduler.get().scheduleDeferred(new ScheduledCommand()
         {
            public void execute()
            {
               doOnLoad();
            }
         });

         ElementIds.assignElementId(this.getElement(), ElementIds.XTERM_WIDGET);
      }
   }
   
   protected void doOnLoad()
   {
      terminal_.fit();
      attachToWidget(getElement(), terminal_);
      terminal_.focus();
      showBanner(); 
   }
   
   public static void load(final Command command)
   {
      xtermLoader_.addCallback(new Callback()
      {
         public void onLoaded()
         {
            xtermFitLoader_.addCallback(new Callback()
            {
               public void onLoaded()
               {
                  if (command != null)
                     command.execute();
               }
            });
         }
     });
   }
   
   private static final native void attachToWidget(Element el, XTermNative terminal)
   /*-{
      el.$RStudioXTermTerminal= terminal;
   }-*/;

   private static final native void detachFromWidget(Element el)
   /*-{
      el.$RStudioXTermTerminal = null;
   }-*/; 

   @Override
   public void onResize()
   {
   }
   
   public void addDataEventHandler(CommandWithArg<String> handler)
   {
      terminal_.onData(handler);
   }
   
   public void setFocus(boolean focused)
   {
      if (focused)
         terminal_.focus();
      else
         terminal_.blur(); 
   }
   
   private XTermNative terminal_;
   private LinkElement currentStyleEl_;
   private ConsoleResources.ConsoleStyles styles_;
   
   private static final ExternalJavaScriptLoader getLoader(StaticDataResource release)
   {
      return getLoader(release, null);
   }
   
   private static final ExternalJavaScriptLoader getLoader(StaticDataResource release,
                                                           StaticDataResource debug)
   {
      if (debug == null || !SuperDevMode.isActive())
         return new ExternalJavaScriptLoader(release.getSafeUri().asString());
      else
         return new ExternalJavaScriptLoader(debug.getSafeUri().asString());
   }
   
   private static final ExternalJavaScriptLoader xtermLoader_ =
         getLoader(XTermResources.INSTANCE.xtermjs(), 
               XTermResources.INSTANCE.xtermjs() /*TODO uncompressed flavor */);

   private static final ExternalJavaScriptLoader xtermFitLoader_ =
         getLoader(XTermResources.INSTANCE.xtermfitjs());
}
