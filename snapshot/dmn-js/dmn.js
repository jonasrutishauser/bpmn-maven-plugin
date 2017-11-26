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
   var viewer = new DmnJS( { container } );
   var xhr = new XMLHttpRequest();
   xhr.onreadystatechange = function() {
      if ( xhr.readyState === 4 ) {
         viewer.importXML(xhr.response, function(err) {
            if (!err) {
               viewer.showDecision(viewer.getDecisions()[0]);
               viewer.on('view.switch', function() {
                  if (viewer.getActiveEditor().table) {
                     var canvas=viewer.get('canvas');
                     canvas.zoom( 1, 'auto' );
                     container.style.height = canvas.viewbox().inner.height + canvas.viewbox().inner.y + 10 + 'px';
                     container.style.width = canvas.viewbox().inner.width + canvas.viewbox().inner.x + 100 + 'px';
                  } else {
                     container.style.height='';
                     container.style.width='';
                  }
               });
            }
         });
      }
   };
   xhr.open( 'GET', url, true );
   xhr.send( null );
}
