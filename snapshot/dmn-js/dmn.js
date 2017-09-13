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
