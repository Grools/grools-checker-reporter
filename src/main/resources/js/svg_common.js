function tooltips_event( tooltips, id, node ){
  id.addEventListener( "mouseenter",  function( event ) {
    tooltips.appendChild( node );
    tooltipsPosition( event, tooltips );;
    tooltips.style.display = 'block';
    
  } );
  id.addEventListener( "mouseleave",  function( event ) {
    tooltips.style.display = 'none';
    tooltips.removeChild(tooltips.firstChild);
  } );
  id.addEventListener( "mousemove",  function( event ) {
    tooltipsPosition( event, tooltips );
  } );
}

function createInformativeNode( text, color ){
  var div = document.createElement('div');
  var p   = document.createElement('p');
  p.style.color = color;
  p.innerHTML   = text;
  div.appendChild( p );
  return div;
}


function tooltipsPosition( event, tooltips ){
  tooltips.style.top = (event.pageY + 40 - window.scrollY)+"px";
  tooltips.style.left= (event.pageX - 30 - window.scrollX)+"px";
}
