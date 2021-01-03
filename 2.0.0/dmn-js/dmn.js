/*
 * Copyright (C) 2017 Jonas Rutishauser
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.txt>.
 */

function showDmn( container, url ) {
   const viewer = new DmnJS( { container } );
   const xhr = new XMLHttpRequest();
   xhr.onreadystatechange = function() {
      if ( xhr.readyState === 4 ) {
         viewer.importXML(xhr.response, function(err) {
            if (!err) {
               const decisionTableViews = viewer.getViews().filter(({type}) => type === 'decisionTable');
               if (decisionTableViews.length === 1) {
                  viewer.open(decisionTableViews[0]);
               }
               viewer.on('views.changed', function() {
                  if ( viewer.getActiveView().type === 'decisionTable') {
                     container.style.height='';
                     container.style.width='';
                  } else {
                     const canvas = viewer.getActiveViewer().get('canvas');
                     container.style.height = canvas.viewbox().inner.height + canvas.viewbox().inner.y + 10 + 'px';
                     container.style.width = canvas.viewbox().inner.width + canvas.viewbox().inner.x + 100 + 'px';
                  }
               });
            }
         });
      }
   };
   xhr.open( 'GET', url, true );
   xhr.send( null );
}
