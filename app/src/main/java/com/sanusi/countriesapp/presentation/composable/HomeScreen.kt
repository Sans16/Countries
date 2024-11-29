package com.sanusi.countriesapp.presentation.composable

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel

import com.sanusi.countriesapp.domain.CountryUI
import com.sanusi.countriesapp.presentation.CountryEvent
import com.sanusi.countriesapp.presentation.CountryUIEvent
import com.sanusi.countriesapp.presentation.CountryViewModel
import com.sanusi.countriesapp.presentation.ui_theme.AppBlack
import com.sanusi.countriesapp.presentation.ui_theme.AppGray
import com.sanusi.countriesapp.presentation.ui_theme.ColorAccent
import com.sanusi.countriesapp.presentation.ui_theme.SecondaryAppBlack
import com.sanusi.countriesapp.utils.Constants.KNOWN_CONTINENTS
import com.sanusi.countriesapp.utils.Constants.getContinentImage
import com.sanusi.graphqltestproject.R
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier,
    viewModel: CountryViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state = viewModel.countryState.collectAsState().value
    val countries = state.countries
    val pagerState = rememberPagerState(pageCount = { countries.size})

    var selectionState by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest {
            when(it) {
                is CountryUIEvent.ShowErrorMessage -> {
                    Toast.makeText(context,it.message,Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Row(
            Modifier
                .padding(
                    horizontal = 24.dp,
                    vertical = 16.dp
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(AppGray)

            ){
                Image(painter = painterResource(id = R.drawable.ic_world),
                    modifier = Modifier
                        .size(30.dp),
                    contentDescription = stringResource(R.string.world_continent_icon_content_description)
                )
            }

            Text(text = stringResource(R.string.header_title).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                color = AppBlack,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier  = Modifier
                    .weight(1f)
                    .padding(
                        end = 8.dp,
                        start = 8.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    ))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(AppBlack)

            ){
                Icon(imageVector = Icons.Filled.Refresh,
                    modifier = Modifier
                        .clickable {
                            selectionState = 0
                            viewModel.onEvent(CountryEvent.RefreshData)
                        }
                        .size(24.dp),
                    tint = Color.White,
                    contentDescription = stringResource(R.string.refresh_icon_content_description))
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
        ) {
            items(KNOWN_CONTINENTS.size) {
                index ->
                ContinentSelector (selectionState,index) {
                    if (selectionState != index) { // Prevent click if already selected
                        selectionState = index
                        viewModel.onEvent(CountryEvent.GetCountryByContinent(KNOWN_CONTINENTS[index]))
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if(state.isLoading) {
                CircularProgressIndicator(color = ColorAccent)
            } else {
                if(state.countries.isNotEmpty()) {
                    CountryPager(pagerState = pagerState,
                        countries = countries)
                } else {
                    EmptyView()
                }
            }
        }
    }
}

@Composable
fun EmptyView() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(R.string.empty_state_header),
            maxLines = 1,
            fontSize = 16.sp,
            overflow = TextOverflow.Ellipsis,
            color = AppBlack,
            fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = stringResource(R.string.empty_state_description),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            color = SecondaryAppBlack,
            textAlign = TextAlign.Center,
            fontSize = 12.sp)
    }
}

@Composable
fun ContinentSelector (
   selectionState : Int,
   index : Int,
   onSelectionClicked : (index : Int) -> Unit
) {
    Box(
        modifier = Modifier
            .defaultMinSize(110.dp, 56.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(
                color = if (selectionState == index) ColorAccent
                else AppGray
            )
            .padding(12.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            {
                onSelectionClicked(index)
            }
    ) {
        Text(
            text = KNOWN_CONTINENTS[index],
            style = MaterialTheme.typography.labelMedium,
            color = AppBlack,
            modifier = Modifier.align(Alignment.Center)
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CountryPager(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    countries: List<CountryUI>
) {
    HorizontalPager(
        state = pagerState,
        pageSpacing = (-100).dp,
        contentPadding = PaddingValues(horizontal = 30.dp),
        modifier = modifier
    ) { page ->
        CountryUI(pagerState, countries[page],page)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CountryUI(pagerState: PagerState,
              country:CountryUI,
              page : Int) {
    val context = LocalContext.current
    val languagesCount = context.resources.getQuantityString(
        R.plurals.country_formated_description,
        country.languages.count(),
        country.name,
        country.continent,
        country.languages.count(),
        country.languages[0])
    Box(
        Modifier
            .graphicsLayer {
                val pageOffset = (
                        (pagerState.currentPage - page) + pagerState
                            .currentPageOffsetFraction
                        ).absoluteValue
                val rotationAngle = if (page < pagerState.currentPage) {
                    lerp(start = -10f, stop = 0f, fraction = 1f - pageOffset.coerceIn(0f, 1f))
                } else {
                    lerp(start = 10f, stop = 0f, fraction = 1f - pageOffset.coerceIn(0f, 1f))
                }

                rotationZ = rotationAngle

                val scale = lerp(
                    start = 0.70f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                )
                scaleX = scale
                scaleY = scale
            }
            .zIndex(if (page == pagerState.currentPage) 1f else 0f)
            .size(340.dp, 440.dp)
            .blur(
                radius = if (page == pagerState.currentPage) 0.dp else 5.dp
            )

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(color = AppBlack)
        ) {
            Column {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(all = 16.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(color = SecondaryAppBlack)

                ) {

                    Text(text = country.emoji,
                        fontSize = 100.sp)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .fillMaxWidth()
                ) {
                    Text(country.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = languagesCount,
                        fontSize = 12.sp,
                        minLines = 4,
                        maxLines = 4,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0XFF787878),
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(
                                color = Color(0xFF2B2B2B),
                                shape = RoundedCornerShape(5.dp)
                            )
                            .weight(1f)
                            .padding(12.dp)
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height = 40.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            val continentImage = getContinentImage(country.continent)

                            Icon(
                                painter = painterResource(continentImage),
                                tint = ColorAccent,
                                contentDescription = stringResource(R.string.continent_icon_content_description),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(
                                text = country.capital,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}