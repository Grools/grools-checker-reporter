
Array.prototype.hasObject = (
  !Array.indexOf ? function (o)
  {
    var l = this.length + 1;
    while (l -= 1)
    {
        if (this[l - 1] === o)
        {
            return true;
        }
    }
    return false;
  } : function (o)
  {
    return (this.indexOf(o) !== -1);
  }
);

function hasClass(el, className) {
  if (el.classList)
    return el.classList.contains(className)
  else
    return !!el.className.match(new RegExp('(\\s|^)' + className + '(\\s|$)'))
}

function addClass(el, className) {
  if (el.classList)
    el.classList.add(className)
  else if (!hasClass(el, className)) el.className += ' ' + className
}

function removeClass(el, className) {
  if (el.classList)
    el.classList.remove(className)
  else if (hasClass(el, className)) {
    var reg = new RegExp('(\\s|^)' + className + '(\\s|$)')
    el.className=el.className.replace(reg, ' ')
  }
}

function tooltips_event( node, title, description, color, graph, path ){
  const tooltips = createInformativeNode(node, title, description, color );
  var isSelected = false;
  document.body.appendChild(tooltips)
  text = node.getElementsByTagName( 'text' )[0]
  text.addEventListener( 'click',  function( event ) {
    tooltips.style.display = 'block';
  } );

  node.addEventListener( 'click',  function( event ) {
    if( event.target.nodeName != 'text' ){
      graph.forEach( function( item ){
          if( path.hasObject( item ) )
              item.style.opacity = 1;
          else
              item.style.opacity = 0.5
      }  );
    }
  } );
}

function startDrag(e) {
    // determine event object
    e=e || window.event;
    // IE uses srcElement, others use target
    targ = e.target ? e.target : e.srcElement;

    if ( hasClass( targ, 'header') ) {
      e.preventDefault();
      // calculate event X, Y coordinates
      offsetX = e.clientX;
      offsetY = e.clientY;

      // assign default values for top and left properties
      if (!targ.parentNode.style.left) {
        var tmp = (e.clientX > 10)? e.clientX - 10 : 0;
        targ.parentNode.style.left = tmp  + 'px';
      };
      if (!targ.parentNode.style.top) {
        var tmp = (e.clientY > 10)? e.clientY - 10 : 0;
          targ.parentNode.style.top = tmp  + 'px';
      };

      // calculate integer values for top and left
      // properties
      coordX = parseInt(targ.parentNode.style.left);
      coordY = parseInt(targ.parentNode.style.top);
      drag = true;
      //targ.parentNode.addEventListener( 'mousemove', dragDiv , false );
    }
    else if( document.selection )
      document.selection.createRange();
    return false;

}

function dragDiv(e) {
    if (drag) {
      e=e || window.event;
      e.stopPropagation()
      e.preventDefault();
      // var targ=e.target?e.target:e.srcElement;
      // move div element
      targ.parentNode.style.left = coordX + e.clientX - offsetX + 'px';
      targ.parentNode.style.top = coordY + e.clientY - offsetY + 'px';
      targ.parentNode.addEventListener ('mouseup' , stopDrag , false);
    };
    return false;
}

function stopDrag() {
    //targ.parentNode.removeEventListener('mousedown', startDrag, false);
    targ.parentNode.removeEventListener('mousemove', dragDiv, false);
    targ.parentNode.removeEventListener('mouseup', stopDrag, false);
    drag = false;
    targ = null;
}

function createInformativeNode( node, title, text, color ){
  var tooltips          = document.createElement('div');
  var header            = document.createElement('div');
  var title_span        = document.createElement('span');
  var button            = document.createElement('button');
  var button_img        = document.createElement('span');
  var p                 = document.createElement('p');
  title_span.innerHTML = title;
  title_span.className = 'title';
  button.appendChild( button_img );
  button.addEventListener( 'click',  function( event ) {
    tooltips.style.display = 'none';
    event.stopPropagation();
  } );
  header.className      = 'header';
  header.appendChild( title_span );
  header.appendChild( button );
  tooltips.id           = 'tooltips-'+node.id;
  tooltips.className    = 'tooltips';
  tooltips.style.display= 'none';
  tooltips.appendChild( header );
  p.innerHTML           = text;
  tooltips.appendChild( p );
  //tooltips.addEventListener( 'mousedown', startDrag , false );
  return tooltips;
}


function tooltipsPosition( event, tooltips, ySshift, xShift ){
  tooltips.style.top = (event.pageY + ySshift - window.scrollY)+'px';
  tooltips.style.left= (event.pageX - xShift  - window.scrollX)+'px';
}

function getPath( node_id, nodes, edges, path ){
    path.push( nodes.filter( n => n.id == node_id )[0] );
    for( var edge_index=0; edge_index < edges.length; edge_index++ ){
        var text        = edges[ edge_index ].getElementsByTagName('title')[0].textContent;
        var relations   = text.split('->'); // 0: source 1: target
        if( node_id == relations[0] ){
            path.push( nodes.filter( n => n.id == relations[1] )[0] );
            path.push( edges[ edge_index ] )
            getPath( relations[1], nodes, edges, path );
        }
    }
    return path;
}
drag=false;
targ=null;
document.onmousedown = startDrag;
document.onmousemove = dragDiv;
