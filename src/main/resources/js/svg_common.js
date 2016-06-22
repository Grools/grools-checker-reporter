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
    var isSelected = false;

    tooltips.addEventListener( "mousedown",  function( event ) {
      event = event || window.event;
      isSelected = true;
    } );

    tooltips.addEventListener( "mousemove",  function( event ) {
      event = event || window.event;
      if( isSelected )
        tooltipsPosition( event, tooltips, -30, 30 );
    }, true );
    tooltips.addEventListener( "mouseup",  function( event ) {
      event = event || window.event;
      isSelected = false;
    }, true );

  node.addEventListener( "mouseenter",  function( event ) {
    event = event || window.event;
    if( ! hasClass(tooltips, "NotClosable" ) && !isSelected ){
      tooltipsPosition( event, tooltips, 40, 30 );
      tooltips.style.display = 'block';
    }
  } );
  node.addEventListener( "mouseleave",  function( event ) {
    event = event || window.event;
    if( ! hasClass(tooltips, "NotClosable" ) && !isSelected ){
      tooltips.style.display = 'none';
    }
  } );
  node.addEventListener( "mousemove",  function( event ) {
    event = event || window.event;
    if( ! hasClass(tooltips, "NotClosable" ) && !isSelected )
      tooltipsPosition( event, tooltips, 40, 30 );
  } );
  node.addEventListener( "click",  function( event ) {
    event = event || window.event;
    if( hasClass(tooltips, "NotClosable" ) ){
      tooltips.style.display = 'none';
      removeClass(tooltips, "NotClosable" );
    }
    else{
      tooltips.style.display = 'block';
      addClass(tooltips, "NotClosable" );
      tooltipsPosition( event, tooltips, 40, 30 );
    }
  } );
}

function createInformativeNode( text, color ){
  var p   = document.createElement('p');
  p.style.color = color;
  p.innerHTML   = text;
  return p;
}


function tooltipsPosition( event, tooltips, ySshift, xShift ){
  tooltips.style.top = (event.pageY + ySshift - window.scrollY)+"px";
  tooltips.style.left= (event.pageX - xShift  - window.scrollX)+"px";
}
