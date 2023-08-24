const API_URL = `http://localhost:8080`;

function fetchTicketsData() {
  fetch(`${API_URL}/api/banks`)
    .then(res => {
      //console.log("res is ", Object.prototype.toString.call(res));
      return res.json();
    })
    .then(data => {
      showTicketList(data);
    })
    .catch(error => {
      console.log(`Error Fetching data : ${error}`);
      document.getElementById('posts').innerHTML = 'Error Loading Bank Data';
    });
}

function fetchTicket(bankId) {
  fetch(`${API_URL}/api/banks/${bankId}`)
    .then(res => {
      //console.log("res is ", Object.prototype.toString.call(res));
      return res.json();
    })
    .then(data => {
      showTicketDetail(data);
    })
    .catch(error => {
      console.log(`Error Fetching data : ${error}`);
      document.getElementById('posts').innerHTML = 'Error Loading Single Bank Data';
    });
}

function parseTicketId() {
  try {
    var url_string = window.location.href.toLowerCase();
    var url = new URL(url_string);
    var ticketid = url.searchParams.get('bankid');
    // var geo = url.searchParams.get("geo");
    // var size = url.searchParams.get("size");
    // console.log(name+ " and "+geo+ " and "+size);
    return ticketid;
  } catch (err) {
    console.log("Issues with Parsing URL Parameter's - " + err);
    return '0';
  }
}
// takes a UNIX integer date, and produces a prettier human string
function dateOf(date) {
  const milliseconds = date * 1000; // 1575909015000
  const dateObject = new Date(milliseconds);
  const humanDateFormat = dateObject.toLocaleString(); //2019-12-9 10:30:15
  return humanDateFormat;
}

function showTicketList(data) {
  // the data parameter will be a JS array of JS objects
  // this uses a combination of "HTML building" DOM methods (the document createElements) and
  // simple string interpolation (see the 'a' tag on title)
  // both are valid ways of building the html.
  const ul = document.getElementById('posts');
  const list = document.createDocumentFragment();

  data.map(function (post) {
    console.log('Bank:', post);
    let li = document.createElement('li');
    let title = document.createElement('h3');
    let body = document.createElement('p');
    title.innerHTML = `<a href="/treefocusdetail.html?bankId=${post.id}">${post.assignedTo.login}</a>`; //What I want to output
    // body.innerHTML = "Hi!";

    li.appendChild(title);
    li.appendChild(body);
    list.appendChild(li);
  });

  ul.appendChild(list);
}

function showTicketDetail(post) {
  // the data parameter will be a JS array of JS objects
  // this uses a combination of "HTML building" DOM methods (the document createElements) and
  // simple string interpolation (see the 'a' tag on title)
  // both are valid ways of building the html.
  const ul = document.getElementById('post');
  const detail = document.createDocumentFragment();

  console.log('User:', post);
  let li = document.createElement('div');
  let title = document.createElement('h2');
  let body = document.createElement('p');
  let by = document.createElement('p');
  title.innerHTML = `${post.assignedTo.login}`;
  body.innerHTML = `Trees owned: ${post.treesowned}`;
  li.appendChild(title);
  li.appendChild(body);
  li.appendChild(by);
  detail.appendChild(li);

  ul.appendChild(detail);
}

function handlePages() {
  let Bankid = parseTicketId();
  console.log('Bank Id: ', Bankid);

  if (Bankid != null) {
    console.log('found a Bank Id');
    fetchTicket(Bankid);
  } else {
    console.log('load all Banks');
    fetchTicketsData();
  }
}

handlePages();
