function showCmmn( container, url ) {
   var viewer = new CmmnJS( { container } );
   var xhr = new XMLHttpRequest();
   xhr.onreadystatechange = function() {
      if ( xhr.readyState === 4 ) {
         viewer.importXML( xhr.response, function( err ) {
            if ( !err ) {
               var canvas = viewer.get( 'canvas' );
               container.style.height = canvas.viewbox().inner.height + canvas.viewbox().inner.y + 10 + 'px';
               container.style.width = canvas.viewbox().inner.width + canvas.viewbox().inner.x + 100 + 'px';
            }
         } );
      }
   };
   xhr.open( 'GET', url, true );
   xhr.send( null );
}
