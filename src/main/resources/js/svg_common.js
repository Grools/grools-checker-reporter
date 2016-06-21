function hasClass(el, className) {
  if (el.classList)
    return el.classList.contains(className)
  else
    return !!el.className.match(new RegExp('(\\s|^)' + className + '(\\s|$)'))
}

function addClass(el, className) {
  if (el.classList)
    el.classList.add(className)
  else if (!hasClass(el, className)) el.className += " " + className
}

function removeClass(el, className) {
  if (el.classList)
    el.classList.remove(className)
  else if (hasClass(el, className)) {
    var reg = new RegExp('(\\s|^)' + className + '(\\s|$)')
    el.className=el.className.replace(reg, ' ')
  }
}

function tooltips_event( node, content ){

    const tooltips        = document.createElement('div');
    tooltips.id           = 'tooltips-'+node.id;
    tooltips.className    = 'grools';
    document.body.appendChild(tooltips);
    tooltips.appendChild( content );
    tooltips.style.display = 'none';

  node.addEventListener( "mouseenter",  function( event ) {
    if( ! hasClass(tooltips, "NotClosable" ) ){
      tooltipsPosition( event, tooltips );
      tooltips.style.display = 'block';
    }
  } );
  node.addEventListener( "mouseleave",  function( event ) {
    if( ! hasClass(tooltips, "NotClosable" ) ){
      tooltips.style.display = 'none';
    }
  } );
  node.addEventListener( "mousemove",  function( event ) {
    if( ! hasClass(tooltips, "NotClosable" ) )
      tooltipsPosition( event, tooltips );
  } );
  node.addEventListener( "click",  function( event ) {
    if( hasClass(tooltips, "NotClosable" ) ){
      tooltips.style.display = 'none';
      removeClass(tooltips, "NotClosable" );
    }
    else{
      tooltips.style.display = 'block';
      addClass(tooltips, "NotClosable" );
    }
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
