
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
    tooltipsPosition( event, tooltips )
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
    const targ = e.target ? e.target : e.srcElement;
    const parent = targ.parentNode;
    if( ! drag ){
        if( hasClass(parent, 'tooltips') && hasClass(targ, 'header') ) {
          e.preventDefault();
          tooltips = parent;
          tooltipsPosition( e, tooltips )
          drag = true;
        }
        else if( window.getSelection ){
            const sel = window.getSelection();
            const range = document.createRange();
            range.selectNode(targ);
            sel.removeAllRanges();
            sel.addRange(range);
        }
        else if( document.selection ){
            document.selection.empty();
            const range = document.body.createTextRange();
            range.moveToElementText(targ);
            range.select();
        }
    }
    return false;
}

function dragDiv(e) {
    if (drag) {
        e=e || window.event;
        e.stopPropagation()
        e.preventDefault();
        // var targ=e.target?e.target:e.srcElement;
        // move div element
        computeOffset(e);
        const coordX = (e.clientX - scrollLeft > 10)? e.clientX - scrollLeft - 10 : 0;
        const coordY = (e.clientY - scrollTop > 10)? e.clientY - scrollTop - 10 : 0;
        tooltips.style.left   = coordX  + 'px';
        tooltips.style.top    = coordY   + 'px';
        tooltips.addEventListener ('mouseup' , stopDrag , false);
    };
    return false;
}

function stopDrag() {
    if( tooltips != null ){
        //targ.parentNode.removeEventListener('mousedown', startDrag, false);
        tooltips.removeEventListener('mousemove', dragDiv, false);
        tooltips.removeEventListener('mouseup', stopDrag, false);
        drag = false;
        tooltips = null;
    }
    else if( drag == true )
        drag = false
}

function createInformativeNode( node, title, text, color ){
  const tooltips       = document.createElement('div');
  const header         = document.createElement('div');
  const title_span     = document.createElement('span');
  const button         = document.createElement('button');
  const button_img     = document.createElement('span');
  const p              = document.createElement('p');
  title_span.innerHTML = title;
  title_span.className = 'title';
  button.appendChild( button_img );
  button.addEventListener( 'click',  function( event ) {
    tooltips.style.display = 'none';
    event.stopPropagation();
    stopDrag();
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

function computeOffset( event ){
    if (doc == null )
        doc = document.documentElement;
    const scrollLeft  = (window.pageXOffset || doc.scrollLeft) - (doc.clientLeft || 0);
    const scrollTop   = (window.pageYOffset || doc.scrollTop)  - (doc.clientTop || 0);
    offsetX     = event.clientX - scrollLeft;
    offsetY     = event.clientY - scrollTop;
}


function tooltipsPosition( event, target ){
  // calculate event X, Y coordinates
  computeOffset( event );
  // assign default values for top and left properties
  if (!target.style.left) {
    const coordX = (offsetX > 10)? offsetX - 10 : 0;
    target.style.left = coordX  + 'px';
  }
  if (!target.parentNode.style.top) {
    const coordY = (offsetY > 10)? offsetY - 10 : 0;
    target.style.top = coordY  + 'px';
  }
}

function getPathChildToParent( node_id, nodes, edges, path ){
    path.push( nodes.filter( n => n.id == node_id )[0] );
    for( var edge_index=0; edge_index < edges.length; edge_index++ ){
        var text        = edges[ edge_index ].getElementsByTagName('title')[0].textContent;
        var relations   = text.split('->'); // 0: source 1: target
        if( node_id == relations[0] ){
            path.push( nodes.filter( n => n.id == relations[1] )[0] );
            path.push( edges[ edge_index ] )
            getPathChildToParent( relations[1], nodes, edges, path );
        }
    }
    return path;
}


function getPathParentToChild( node_id, nodes, edges, path ){
    const parent = nodes.filter( n => n.id == node_id )[0];
    path.push( parent );
    for( var edge_index=0; edge_index < edges.length; edge_index++ ){
        const text        = edges[ edge_index ].getElementsByTagName('title')[0].textContent;
        const relations   = text.split('->'); // 0: child 1: parent
        if( node_id == relations[1] ){
            const child = nodes.filter( n => n.id == relations[0] )[0];
            path.push( child );
            path.push( edges[ edge_index ] )
            getPathParentToChild( relations[0], nodes, edges, path );
        }
    }
    return path;
}

doc         = null;
scrollLeft  = 0;
scrollTop   = 0;
offsetX     = 0;
offsetY     = 0;
tooltips    = null;
drag        = false;
targ        = null;
document.onmousedown = startDrag;
document.onmousemove = dragDiv;
