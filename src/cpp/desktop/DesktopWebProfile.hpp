/*
 * DesktopWebPage.hpp
 *
 * Copyright (C) 2009-17 by RStudio, Inc.
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

#ifndef RSTUDIO_DESKTOP_WEB_PROFILE_HPP
#define RSTUDIO_DESKTOP_WEB_PROFILE_HPP

#include <QWebEngineProfile>

namespace rstudio {
namespace desktop {

class WebProfile : public QWebEngineProfile
{
   Q_OBJECT

public:
   explicit WebProfile(QObject* parent = nullptr);
};

} // end namespace desktop
} // end namespace rstudio

#endif /* RSTUDIO_DESKTOP_WEB_PROFILE_HPP */
